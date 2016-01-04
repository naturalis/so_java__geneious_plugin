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
 * <p>Java class for DNALabProject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DNALabProject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="projectID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="versionNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="amplification" type="{http://data.naturalis.nl/lims2}Amplification"/>
 *         &lt;element name="sequencing" type="{http://data.naturalis.nl/lims2}Sequencing"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DNALabProject", propOrder = {
    "projectID",
    "versionNumber",
    "amplification",
    "sequencing"
})
public class DNALabProject {

    @XmlElement(required = true)
    protected String projectID;
    @XmlElement(required = true)
    protected String versionNumber;
    @XmlElement(required = true)
    protected Amplification amplification;
    @XmlElement(required = true)
    protected Sequencing sequencing;

    /**
     * Gets the value of the projectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * Sets the value of the projectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectID(String value) {
        this.projectID = value;
    }

    /**
     * Gets the value of the versionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the value of the versionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionNumber(String value) {
        this.versionNumber = value;
    }

    /**
     * Gets the value of the amplification property.
     * 
     * @return
     *     possible object is
     *     {@link Amplification }
     *     
     */
    public Amplification getAmplification() {
        return amplification;
    }

    /**
     * Sets the value of the amplification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amplification }
     *     
     */
    public void setAmplification(Amplification value) {
        this.amplification = value;
    }

    /**
     * Gets the value of the sequencing property.
     * 
     * @return
     *     possible object is
     *     {@link Sequencing }
     *     
     */
    public Sequencing getSequencing() {
        return sequencing;
    }

    /**
     * Sets the value of the sequencing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sequencing }
     *     
     */
    public void setSequencing(Sequencing value) {
        this.sequencing = value;
    }

}
