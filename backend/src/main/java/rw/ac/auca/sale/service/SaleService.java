package rw.ac.auca.sale.service;

import rw.ac.auca.sale.domain.Sale;

import java.util.List;
import java.util.UUID;

public interface SaleService {
    Sale registerSale(Sale theSale);
    Sale requestSale(Sale theSale);
    Sale approveSale(UUID saleId);
    Sale rejectSale(UUID saleId);
    Sale findSaleById(Sale theSale);
    List<Sale> findAllSales();
    List<Sale> findPendingSales();
    List<Sale> findMySales();
    void deleteSale(Sale theSale);
}
