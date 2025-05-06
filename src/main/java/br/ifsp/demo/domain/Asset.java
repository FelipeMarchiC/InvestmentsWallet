package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    private String name;
    @Column(name = "asset_type")
    @Enumerated(EnumType.STRING)
    private AssetType assetType;
    private double profitability;
    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    public Asset(String name, AssetType assetType, double profitability, LocalDate maturityDate) {
        verifyAsset(name, assetType, profitability, maturityDate);
        this.id = UUID.randomUUID();
        this.name = name;
        this.assetType = assetType;
        this.profitability = profitability;
        this.maturityDate = maturityDate;
    }

    public Asset() {
        this.id = UUID.randomUUID();
    }

    private void verifyAsset(String name, AssetType assetType, double profitability, LocalDate maturityDate){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Asset name cannot be null or blank");
        if (assetType == null) throw new IllegalArgumentException("Asset type cannot be null");
        if (profitability <= 0) throw new IllegalArgumentException("Asset profitability must be greater than zero");
        if (profitability < 0.01) throw new IllegalArgumentException("Asset profitability must be greater or equal 0.01");
        if (maturityDate == null) throw new IllegalArgumentException("Asset maturity date cannot be null");
    }

    @Override
    public String toString() {
        return "Asset name = "
                + name
                + " | Type: " + assetType
                + " | Asset profitability = "
                + String.format("%.2f%%", profitability * 100)
                + " | Asset maturity date = "
                +  DateFormatter.formatDateToSlash(maturityDate);
    }
}
