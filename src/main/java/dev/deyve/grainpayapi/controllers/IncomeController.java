package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.IncomeDTO;
import dev.deyve.grainpayapi.services.IService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/incomes")
public class IncomeController implements IController<IncomeDTO> {

    private static final Logger logger = LoggerFactory.getLogger(IncomeController.class);

    private final IService<IncomeDTO> incomeService;

    /**
     * Get Incomes by Page
     *
     * @return List<IncomeDTO> List of Incomes
     */
    @Override
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        logger.info("GRAIN-API: Find incomes by page {}, size {} and sort by {}", page, size, sort);

        Page<IncomeDTO> incomes = incomeService.findAll(pageable);

        return new ResponseEntity<>(incomes.getContent(), HttpStatus.OK);
    }

    /**
     * Post Income
     *
     * @param incomeDTO New Income
     * @return ResponseEntity
     */
    @Override
    @PostMapping
    public ResponseEntity<IncomeDTO> post(@Valid @RequestBody IncomeDTO incomeDTO) {

        logger.info("GRAIN-API: Save income: {}", incomeDTO);

        IncomeDTO incomeSaved = incomeService.save(incomeDTO);

        return new ResponseEntity<>(incomeSaved, HttpStatus.CREATED);
    }

    /**
     * Get Income by Id
     *
     * @param id Long
     * @return ResponseEntity
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<IncomeDTO> get(@PathVariable Long id) {

        logger.info("GRAIN-API: Get income by id: {}", id);

        IncomeDTO income = incomeService.findById(id);

        return new ResponseEntity<>(income, HttpStatus.OK);
    }

    /**
     * Update Income by Id
     *
     * @param id        Long
     * @param incomeDTO IncomeDTO
     * @return ResponseEntity
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<IncomeDTO> put(@PathVariable Long id, @Valid @RequestBody IncomeDTO incomeDTO) {

        logger.info("GRAIN-API: Update income by id: {}", id);

        IncomeDTO incomeUpdated = incomeService.updateById(id, incomeDTO);

        return new ResponseEntity<>(incomeUpdated, HttpStatus.OK);
    }

    /**
     * Delete Income by Id
     *
     * @param id Long
     * @return ResponseEntity
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        logger.info("GRAIN-API: Delete income by id: {}", id);

        incomeService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
