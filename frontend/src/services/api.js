import axios from 'axios'

const resolveApiBaseUrl = () => {
  const configuredUrl = import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL
  if (configuredUrl) return configuredUrl.replace(/\/+$/, '')

  if (typeof window !== 'undefined') {
    const host = window.location.hostname
    if (host === 'localhost' || host === '127.0.0.1') return 'http://localhost:8080/api'
    return `${window.location.origin.replace(/\/+$/, '')}/api`
  }

  return 'http://localhost:8080/api'
}

export const API_BASE_URL = resolveApiBaseUrl()

export const getWebSocketUrl = () => {
  const origin = API_BASE_URL.replace(/\/api\/?$/, '')
  if (!origin) return `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.hostname}:8080/ws`
  return `${origin.replace(/^http:/, 'ws:').replace(/^https:/, 'wss:')}/ws`
}

export const describeApiError = (error) => {
  const backendMessage = error?.response?.data?.message || error?.response?.data?.error || error?.response?.data?.details
  if (error?.code === 'ERR_NETWORK') return 'Unable to reach the backend API. Check the Render backend URL, CORS settings, and server status.'
  if (error?.response?.status === 400) return backendMessage || 'Invalid request payload.'
  if (error?.response?.status === 401) return 'Your session expired. Please log in again.'
  if (error?.response?.status === 403) return 'You are not allowed to perform this action.'
  if (error?.response?.status === 404) return 'The requested backend endpoint is unavailable.'
  if (error?.response?.status >= 500) return backendMessage || 'The backend encountered an error. Please try again.'
  return backendMessage || error?.message || 'Request failed.'
}

export const api = axios.create({ baseURL: API_BASE_URL, timeout: 15000 })
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('zomiggyToken')
  if (token?.trim()) config.headers.Authorization = `Bearer ${token.trim()}`
  if (!config.url?.includes('/auth/')) {
    let data = config.data
    if (typeof data === 'string') { try { data = JSON.parse(data) } catch { data = '[unparsed request body]' } }
    if (data && typeof data === 'object') data = { ...data, ...(Object.hasOwn(data, 'pin') ? { pin: '[redacted]' } : {}) }
    console.debug('API request', { method: config.method?.toUpperCase(), url: `${config.baseURL || ''}${config.url || ''}`, hasAuthorization: Boolean(token?.trim()), data })
  }
  return config
})

api.interceptors.response.use((response) => response, (error) => {
  console.error('API response failed', { method: error.config?.method?.toUpperCase(), url: `${error.config?.baseURL || ''}${error.config?.url || ''}`, status: error.response?.status, response: error.response?.data, code: error.code })
  if ([401, 403].includes(error.response?.status) && !error.config?.url?.includes('/auth/')) {
    localStorage.removeItem('zomiggyToken')
    localStorage.removeItem('isLoggedIn')
    window.dispatchEvent(new Event('zomiggy-auth-expired'))
  }
  return Promise.reject(Object.assign(error, { userMessage: describeApiError(error) }))
})

export const authService = {
  requestOtp: async (mobile) => (await api.post('/auth/request-otp', { mobile })).data,
  verifyOtp: async (mobile, otp) => (await api.post('/auth/verify-otp', { mobile, otp })).data,
}

export const catalogService = {
  restaurants: async (params = {}) => (await api.get('/restaurants', { params })).data.restaurants.map((item) => ({ ...item, image: item.imageUrl })),
  dishes: async (params = {}) => (await api.get('/dishes', { params })).data.dishes.map((item) => ({ ...item, image: item.imageUrl })),
  coupons: async () => (await api.get('/coupons')).data.coupons,
}

export const userService = {
  profile: async () => (await api.get('/users/me')).data.user,
  updateProfile: async (body) => (await api.patch('/users/me', body)).data.user,
}

export const locationService = {
  search: async (query) => (await api.get('/locations/search', { params: { q: query } })).data,
}

export const orderService = {
  history: async () => (await api.get('/orders')).data.orders,
  create: async (body) => (await api.post('/orders', body)).data.order,
  tracking: async (id) => (await api.get(`/orders/${id}/tracking`)).data,
}

export const paymentService = {
  create: async (body) => (await api.post('/payments/split', body)).data.splitPayment,
  get: async (id) => (await api.get(`/payments/split/${id}`)).data.splitPayment,
  pay: async (id, participantId, body) => (await api.post(`/payments/split/${id}/participants/${participantId}/pay`, body)).data.splitPayment,
  complete: async (id) => (await api.post(`/payments/split/${id}/complete`)).data.splitPayment,
}

export const deliveryService = {
  accept: async (orderId) => (await api.post(`/delivery/orders/${orderId}/accept`)).data.delivery,
  reject: async (orderId) => (await api.post(`/delivery/orders/${orderId}/reject`)).data.delivery,
  status: async (orderId, value) => (await api.post(`/delivery/orders/${orderId}/status/${value}`)).data.delivery,
  location: async (orderId, body) => (await api.post(`/delivery/orders/${orderId}/location`, body)).data.delivery,
}
