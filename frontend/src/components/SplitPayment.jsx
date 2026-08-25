import { useEffect, useState } from 'react'
import { paymentService } from '../services/api'

const money = (value) => `₹${Number(value).toFixed(2)}`
const previewSession = (total, numberOfParticipants) => {
  const totalPaise = Math.round(Number(total || 0) * 100)
  const base = Math.floor(totalPaise / numberOfParticipants)
  const remainder = totalPaise % numberOfParticipants
  return { splitPaymentId: null, totalAmount: Number(total || 0), amountPaid: 0, remainingAmount: Number(total || 0), status: 'PENDING', numberOfParticipants, participants: Array.from({ length: numberOfParticipants }, (_, index) => ({ participantId: `preview-${index + 1}`, paymentLink: '', upiId: '', amount: (base + (index === numberOfParticipants - 1 ? remainder : 0)) / 100, status: 'PENDING' })) }
}

export default function SplitPayment({ checkout, onClose, onCancel = onClose, onComplete, toast }) {
  const [people, setPeople] = useState(3)
  const [session, setSession] = useState(null)
  const [coupon, setCoupon] = useState(checkout?.couponCode || '')
  const [pins, setPins] = useState([])
  const [upis, setUpis] = useState([])
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [couponError, setCouponError] = useState('')
  const [participantErrors, setParticipantErrors] = useState([])

  const load = async (participantCount = people, couponCode = coupon, couponAction = false) => {
    if (!checkout?.cart?.length || participantCount < 1 || participantCount > 10) return setError('Add items and choose between 1 and 10 participants')
    setBusy(true); setError(''); if (couponAction) setCouponError('')
    try {
      const savedId = localStorage.getItem('zomiggySplitPaymentId')
      let saved = null
      if (savedId) {
        try { saved = await paymentService.get(savedId) } catch { localStorage.removeItem('zomiggySplitPaymentId') }
      }
      const savedMatchesCart = saved && saved.numberOfParticipants === participantCount && Math.abs(Number(saved.totalAmount) - checkout.total) < 0.01 && (couponCode || '') === (checkout.couponCode || '')
      const result = savedMatchesCart ? saved : await paymentService.create({ items: checkout.cart.map((item) => ({ dishId: item.id, quantity: item.qty })), couponCode: couponCode || null, numberOfParticipants: participantCount })
      setSession(result); setPeople(result.numberOfParticipants); setUpis(result.participants.map((participant, index) => participant.upiId || upis[index] || '')); setPins(result.participants.map((_, index) => pins[index] || ''))
      setParticipantErrors(result.participants.map(() => ''))
      localStorage.setItem('zomiggySplitPaymentId', result.splitPaymentId)
    } catch (err) { if (!session) setSession(previewSession(checkout.total, participantCount)); if (couponAction) setCouponError(err.response?.data?.message || (err.message === 'Network Error' ? 'Unable to connect to payment service.' : 'Unable to apply coupon. Please try again.')); else setError(err.message === 'Network Error' ? 'Unable to connect to payment service.' : (err.response?.data?.message || err.message || 'Could not start split payment')) } finally { setBusy(false) }
  }
  useEffect(() => { load() }, [])
  const changePeople = (value) => {
    const next = Number(value)
    setPeople(value)
    if (!Number.isInteger(next) || next < 1 || next > 10) return setError('Choose between 1 and 10 participants')
    localStorage.removeItem('zomiggySplitPaymentId'); load(next)
  }
  const pay = async (participant, index) => {
    const upiId = upis[index]?.trim() || ''
    const pin = pins[index] || ''
    if (!/^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+$/.test(upiId)) return setParticipantErrors((values) => values.map((value, current) => current === index ? 'Invalid UPI ID' : value))
    if (!/^\d{4}$/.test(pin)) return setParticipantErrors((values) => values.map((value, current) => current === index ? 'Enter a valid 4-digit UPI PIN' : value))
    setBusy(true); setError('')
    setParticipantErrors((values) => values.map((value, current) => current === index ? '' : value))
    try {
      const result = await paymentService.pay(session.splitPaymentId, participant.participantId, { upiId, pin })
      setSession(result); setParticipantErrors(result.participants.map(() => ''))
      setUpis(result.participants.map((item) => item.upiId || '')); setPins(result.participants.map(() => ''))
      if (result.status === 'PAID') { onComplete({ ...result, couponCode: coupon || null }); toast('All split payments completed') }
    } catch (err) { setParticipantErrors((values) => values.map((value, current) => current === index ? (err.response?.data?.message || 'Payment failed. Please retry.') : value)) } finally { setBusy(false) }
  }
  const cancel = () => { localStorage.removeItem('zomiggySplitPaymentId'); onCancel(); onClose() }
  const applyCoupon = () => { localStorage.removeItem('zomiggySplitPaymentId'); load(people, coupon, true) }
  const removeCoupon = () => { setCoupon(''); localStorage.removeItem('zomiggySplitPaymentId'); load(people, '') }
  return <div className="split-payment-modal">
    <p className="text-sm text-muted">Each person completes their own simulated payment. PINs are used only for validation and are never stored.</p>
    <div className="split-summary"><div><span>Total Amount</span><b>{money(checkout?.total ?? 0)}</b></div><label>Split between<input type="number" min="1" max="10" value={people} onChange={(event) => changePeople(event.target.value)} /></label></div>
    <div className="coupon-entry"><input value={coupon} onChange={(event) => { setCoupon(event.target.value.toUpperCase()); setCouponError('') }} placeholder="Coupon Code" aria-label="Split payment coupon code" /><button className="btn btn-outline" onClick={applyCoupon}>Apply</button>{coupon && <button className="btn btn-text" onClick={removeCoupon}>Remove Coupon</button>}</div>
    {couponError && <p className="form-error text-sm">{couponError}</p>}
    {session && <><div className="text-primary font-bold text-lg mb-2">Split Payment Summary</div><div className="text-sm mb-2">Original Amount: <b>{money(checkout?.total || 0)}</b><br />Coupon Discount: <b>-{money(Math.max(0, Number(checkout?.total || 0) - Number(session.totalAmount)))}</b><br />Final Amount: <b>{money(session.totalAmount)}</b></div><div className="split-participants">{session.participants.map((participant, index) => <div className="split-participant" key={participant.participantId}><div className="section-heading"><b>Person {index + 1}</b><span className={participant.status === 'PAID' ? 'text-success' : 'text-muted'}>{participant.status === 'PAID' ? 'Paid ✓' : 'Pending'}</span></div><label className="text-sm">UPI ID<input value={upis[index] || ''} disabled={participant.status === 'PAID'} onChange={(event) => setUpis((values) => values.map((value, current) => current === index ? event.target.value : value))} placeholder="person@upi" aria-label={`UPI ID for Person ${index + 1}`} /></label>{participantErrors[index] && <p className="form-error text-sm">{participantErrors[index]}</p>}<label className="text-sm">UPI PIN<input type="password" inputMode="numeric" maxLength="4" value={pins[index] || ''} disabled={participant.status === 'PAID'} onChange={(event) => setPins((values) => values.map((value, current) => current === index ? event.target.value.replace(/\D/g, '').slice(0, 4) : value))} placeholder="4-digit PIN" aria-label={`UPI PIN for Person ${index + 1}`} /></label><p className="text-sm">Amount to Pay: <b>{money(participant.amount)}</b></p>{index > 0 && <p className="text-sm text-muted">Payment link: <span>{participant.paymentLink}</span></p>}<button className="btn btn-primary w-100 mt-2" disabled={busy || !session.splitPaymentId || participant.status === 'PAID'} onClick={() => pay(participant, index)}>{participant.status === 'PAID' ? 'Paid' : session.splitPaymentId ? `Validate Payment ${money(participant.amount)}` : 'Payment service unavailable'}</button></div>)}</div><p className="text-sm mt-2">Total Paid: <b>{money(session.amountPaid)}</b> | Remaining: <b>{money(session.remainingAmount)}</b></p></>}
    {error && <p className="form-error mt-2">{error}</p>}
    <button className="btn btn-outline w-100 mt-2" onClick={cancel}>Cancel Split Payment</button>
  </div>
}
