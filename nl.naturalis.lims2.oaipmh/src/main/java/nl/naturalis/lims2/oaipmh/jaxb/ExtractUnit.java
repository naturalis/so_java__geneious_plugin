//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.04 at 10:27:13 AM CET 
//


package nl.naturalis.lims2.oaipmh.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtractUnit complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtractUnit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unitID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="associatedUnitID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="institutePlateID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="platePosition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtractUnit", propOrder = {
    "unitID",
    "associatedUnitID",
    "institutePlateID",
    "platePosition"
})
public class ExtractUnit {

    @XmlElement(required = true)
    protected String unitID;
    @XmlElement(required = true)
    protected String associatedUnitID;
    @XmlElement(required = true)
    protected String institutePlateID;
    @XmlElement(required = true)
    protected String platePosition;

    /**
     * Gets the value of the unitID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitID() {
        return unitID;
    }

    /**
     * Sets the value of the unitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitID(String value) {
        this.unitID = value;
    }

    /**
     * Gets the value of the associatedUnitID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssociatedUnitID() {
        return associatedUnitID;
    }

    /**
     * Sets the value of the associatedUnitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssociatedUnitID(String value) {
        this.associatedUnitID = value;
    }

    /**
     * Gets the value of the institutePlateID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutePlateID() {
        return institutePlateID;
    }

    /**
     * Sets the value of the institutePlateID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutePlateID(String value) {
        this.institutePlateID = value;
    }

    /**
     * Gets the value of the platePosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlatePosition() {
        return platePosition;
    }

    /**
     * Sets the value of the platePosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlatePosition(String value) {
        this.platePosition = value;
    }

}