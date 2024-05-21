package jp.co.metateam.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.RentalManage;

@Repository
public interface RentalManageRepository extends JpaRepository<RentalManage, Long> {
    List<RentalManage> findAll();
    @Query("select r from RentalManage r where r.stock.id = ?1 and r.status in (0,1)")
    List<RentalManage> findByStockIdAndStatus(String newStockId);


	Optional<RentalManage> findById(Long id);
}
