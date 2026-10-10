package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.sale.domain.Sale;
import rw.ac.auca.sale.service.SaleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public List<Sale> findAllRecords() {
        return saleService.findAllSales();
    }

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public List<Sale> findPending() {
        return saleService.findPendingSales();
    }

    @GetMapping("/mine")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public List<Sale> findMine() {
        return saleService.findMySales();
    }

    @GetMapping("/search-by-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public Sale findById(@PathVariable UUID id) {
        Sale probe = new Sale();
        probe.setId(id);
        return saleService.findSaleById(probe);
    }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public Sale requestSale(@RequestBody Sale theSale) {
        return saleService.requestSale(theSale);
    }

    @PostMapping("/approve/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public Sale approveSale(@PathVariable UUID id) {
        return saleService.approveSale(id);
    }

    @PostMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public Sale rejectSale(@PathVariable UUID id) {
        return saleService.rejectSale(id);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Sale createSale(@RequestBody Sale theSale) {
        return saleService.registerSale(theSale);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSale(@PathVariable UUID id) {
        Sale probe = new Sale();
        probe.setId(id);
        saleService.deleteSale(probe);
        return "Deleted";
    }
}
