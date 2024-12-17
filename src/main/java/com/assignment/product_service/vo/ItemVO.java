package com.assignment.product_service.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
//import jakarta.xml.bind.annotation.XmlRootElement;
//import jakarta.xml.bind.annotation.XmlElement;
//import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@XmlRootElement // Indicates this class can be marshalled to XML/JSON
public class ItemVO {
    //@XmlAttribute // Marks this as an attribute in XML (or JSON key)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    //@XmlElement // Marks this as an element in XML (or JSON key)
    private String name;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must not exceed 100")
    //@XmlElement
    private int quantity;

    public ItemVO() {}

    public ItemVO(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
