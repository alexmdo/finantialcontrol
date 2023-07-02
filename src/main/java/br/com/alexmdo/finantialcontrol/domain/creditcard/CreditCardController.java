package br.com.alexmdo.finantialcontrol.domain.creditcard;

import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.creditcard.dto.CreditCardDto;
import br.com.alexmdo.finantialcontrol.infra.BaseController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/users/me/credit-cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class CreditCardController extends BaseController {

    private final CreditCardService creditCardService;
    private final CreditCardMapper creditCardMapper;

    @PostMapping
    public CompletableFuture<ResponseEntity<CreditCardDto>> createCreditCardAsync(@Valid @RequestBody CreditCardCreateRequestDto createRequestDto) {
        var creditCard = creditCardMapper.toEntity(createRequestDto);
        return creditCardService.createCreditCardForUserAsync(creditCard, super.getPrincipal())
                .thenApply(createdCreditCard -> {
                    var creditCardDto = creditCardMapper.toDto(createdCreditCard);
                    return ResponseEntity.status(HttpStatus.CREATED).body(creditCardDto);
                });
    }

    @PostMapping("/{id}/archive")
    public CompletableFuture<ResponseEntity<CreditCardDto>> archiveCreditCardAsync(@PathVariable Long id) {
        return creditCardService
                .archiveCreditCardForUserAsync(id, super.getPrincipal())
                .thenApply(archivedCreditCard -> {
                    var creditCardDto = creditCardMapper.toDto(archivedCreditCard);
                    return ResponseEntity.ok(creditCardDto);
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteCreditCardAsync(@PathVariable Long id) {
        return creditCardService
                .deleteCreditCardForUserAsync(id, super.getPrincipal())
                .thenApply(__ -> ResponseEntity.noContent().build());
    }

}
