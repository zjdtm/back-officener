package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.dto.BuildingWithCompaniesDTO;
import fastcampus.team7.Livable_officener.dto.CompanyDTO;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;

    public List<BuildingWithCompaniesDTO> getBuildingWithCompanies(String keyword) {

        List<Building> buildings = buildingRepository.findBuildingsByNameContaining(keyword);

        List<BuildingWithCompaniesDTO> buildingDTOs = new ArrayList<>();

        for (Building building : buildings) {
            String address = getAddress(building);

            BuildingWithCompaniesDTO buildingDTO = BuildingWithCompaniesDTO.builder()
                    .id(building.getId())
                    .buildingName(building.getName())
                    .buildingAddress(address)
                    .companies(getCompanies(companyRepository.findCompaniesByBuildingName(building.getName())))
                    .build();

            buildingDTOs.add(buildingDTO);
        }

        return buildingDTOs;
    }

    private String getAddress(Building building) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(building.getRegion());
        sj.add(building.getCity());
        sj.add(building.getStreet());
        sj.add(building.getZipcode());
        return sj.toString();
    }

    private List<CompanyDTO> getCompanies(List<Company> companies) {
        List<CompanyDTO> companyDTOs = new ArrayList();
        for (Company company : companies) {
            CompanyDTO companyDTO = new CompanyDTO(
                    company.getId(),
                    company.getName(),
                    company.getUnit()
            );
            companyDTOs.add(companyDTO);
        }
        return companyDTOs;
    }

}
