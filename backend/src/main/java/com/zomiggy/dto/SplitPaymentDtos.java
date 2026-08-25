package com.zomiggy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class SplitPaymentDtos {
    private SplitPaymentDtos() {}
    public record CreateRequest(@NotEmpty @Valid List<ApiDtos.OrderItemRequest> items, String couponCode, @Min(1) @Max(10) int numberOfParticipants) {}
    public record ParticipantRequest(@NotBlank @Pattern(regexp = "^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+$") String upiId, @NotBlank @Pattern(regexp = "^\\d{4}$") String pin) {}
    public record ParticipantResponse(Long participantId, String paymentLink, String upiId, BigDecimal amount, String status, Instant paidAt) {}
    public record Response(String splitPaymentId, BigDecimal totalAmount, int numberOfParticipants, BigDecimal amountPaid, BigDecimal remainingAmount, String status, List<ParticipantResponse> participants) {}
}
