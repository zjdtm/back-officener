package fastcampus.team7.Livable_officener.dto.delivery;

import fastcampus.team7.Livable_officener.domain.Bank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class BankListResponseDTO {
    private List<Bank> bankList;

    public BankListResponseDTO(List<Bank> bankList) {
        this.bankList = bankList;
    }
}
