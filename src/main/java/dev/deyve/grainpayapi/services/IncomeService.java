package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.exceptions.IncomeNotFoundException;
import dev.deyve.grainpayapi.mappers.IncomeMapper;
import dev.deyve.grainpayapi.models.Income;
import dev.deyve.grainpayapi.repositories.IncomeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IncomeService implements IService<IncomeDTO> {

    private static final Logger logger = LoggerFactory.getLogger(IncomeService.class);

    private final IncomeMapper incomeMapper;
    private final IncomeRepository incomeRepository;

    /**
     * Find All Incomes
     *
     * @param pageable Pageable
     * @return Page of IncomeDTO
     */
    @Override
    public Page<IncomeDTO> findAll(Pageable pageable) {
        Page<Income> incomes = incomeRepository.findAll(pageable);

        logger.debug("GRAIN-API: Incomes: {}", incomes);

        return incomes.map(incomeMapper::toDTO);
    }

    /**
     * Save Income
     *
     * @param incomeDTO IncomeDTO
     * @return IncomeDTO
     */
    @Override
    public IncomeDTO save(IncomeDTO incomeDTO) {
        Income income = incomeMapper.toEntity(incomeDTO);

        Income incomeSaved = incomeRepository.save(income);

        logger.debug("GRAIN-API: Income Saved: {}", incomeSaved);

        return incomeMapper.toDTO(incomeSaved);
    }

    /**
     * Find Income by Id
     *
     * @param id Long
     * @return IncomeDTO
     */
    @Override
    public IncomeDTO findById(Long id) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));

        logger.debug("GRAIN-API: Income: {}", income);

        return incomeMapper.toDTO(income);
    }

    /**
     * Update Income by Id
     *
     * @param id        Long
     * @param incomeDTO IncomeDTO
     * @return IncomeDTO
     */
    @Override
    public IncomeDTO updateById(Long id, IncomeDTO incomeDTO) {

        checkId(id, incomeDTO);

        Income income = incomeMapper.toEntity(incomeDTO);

        Income incomeUpdated = incomeRepository.save(income);

        logger.debug("GRAIN-API: Income Updated: {}", incomeUpdated);

        return incomeMapper.toDTO(incomeUpdated);
    }

    /**
     * Delete Income by Id
     *
     * @param id Long
     */
    @Override
    public void deleteById(Long id) {

        logger.debug("GRAIN-API: Delete income by id {}", id);

        incomeRepository.findById(id).orElseThrow(() -> new IncomeNotFoundException("Income not found!"));

        incomeRepository.deleteById(id);
    }

    /**
     * Check Id
     *
     * @param id        Long
     * @param incomeDTO IncomeDTO
     */
    private void checkId(Long id, IncomeDTO incomeDTO) {
        if (!id.equals(incomeDTO.getId())) {
            throw new IllegalArgumentException("Id mismatch");
        }
    }
}
