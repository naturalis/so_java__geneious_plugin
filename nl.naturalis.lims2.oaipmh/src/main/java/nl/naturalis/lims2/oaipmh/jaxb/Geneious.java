//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.21 at 11:29:42 AM CET 
//


package nl.naturalis.lims2.oaipmh.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="specimen" type="{http://data.naturalis.nl/lims2}Specimen"/>
 *         &lt;element name="dnaSlide" type="{http://data.naturalis.nl/lims2}DNASlide"/>
 *         &lt;element name="dnaExtract" type="{http://data.naturalis.nl/lims2}DNAExtract"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "specimen",
    "dnaSlide",
    "dnaExtract"
})
@XmlRootElement(name = "Geneious")
public class Geneious {

    protected Specimen specimen;
    protected DNASlide dnaSlide;
    protected DNAExtract dnaExtract;

    /**
     * Gets the value of the specimen property.
     * 
     * @return
     *     possible object is
     *     {@link Specimen }
     *     
     */
    public Specimen getSpecimen() {
        return specimen;
    }

    /**
     * Sets the value of the specimen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Specimen }
     *     
     */
    public void setSpecimen(Specimen value) {
        this.specimen = value;
    }

    /**
     * Gets the value of the dnaSlide property.
     * 
     * @return
     *     possible object is
     *     {@link DNASlide }
     *     
     */
    public DNASlide getDnaSlide() {
        return dnaSlide;
    }

    /**
     * Sets the value of the dnaSlide property.
     * 
     * @param value
     *     allowed object is
     *     {@link DNASlide }
     *     
     */
    public void setDnaSlide(DNASlide value) {
        this.dnaSlide = value;
    }

    /**
     * Gets the value of the dnaExtract property.
     * 
     * @return
     *     possible object is
     *     {@link DNAExtract }
     *     
     */
    public DNAExtract getDnaExtract() {
        return dnaExtract;
    }

    /**
     * Sets the value of the dnaExtract property.
     * 
     * @param value
     *     allowed object is
     *     {@link DNAExtract }
     *     
     */
    public void setDnaExtract(DNAExtract value) {
        this.dnaExtract = value;
    }

}
