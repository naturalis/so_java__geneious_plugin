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
 * <p>Java class for DNAExtract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DNAExtract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unit" type="{http://data.naturalis.nl/lims2}ExtractUnit"/>
 *         &lt;element name="dnaLabProject" type="{http://data.naturalis.nl/lims2}DNALabProject"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DNAExtract", propOrder = {
    "unit",
    "dnaLabProject"
})
public class DNAExtract {

    @XmlElement(required = true)
    protected ExtractUnit unit;
    @XmlElement(required = true)
    protected DNALabProject dnaLabProject;

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link ExtractUnit }
     *     
     */
    public ExtractUnit getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtractUnit }
     *     
     */
    public void setUnit(ExtractUnit value) {
        this.unit = value;
    }

    /**
     * Gets the value of the dnaLabProject property.
     * 
     * @return
     *     possible object is
     *     {@link DNALabProject }
     *     
     */
    public DNALabProject getDnaLabProject() {
        return dnaLabProject;
    }

    /**
     * Sets the value of the dnaLabProject property.
     * 
     * @param value
     *     allowed object is
     *     {@link DNALabProject }
     *     
     */
    public void setDnaLabProject(DNALabProject value) {
        this.dnaLabProject = value;
    }

}
