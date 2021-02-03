//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.12.26 at 02:52:31 PM IRKT 
//


package ru.sibdigital.proccovid.dto.egrul040601;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения об адресообразующем элементе район
 * 
 * <p>Java class for РайонТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="РайонТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="ТипРайон" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="100"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="НаимРайон" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="255"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "\u0420\u0430\u0439\u043e\u043d\u0422\u0438\u043f")
public class РайонТип {

    @XmlAttribute(name = "\u0422\u0438\u043f\u0420\u0430\u0439\u043e\u043d", required = true)
    protected String типРайон;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u0420\u0430\u0439\u043e\u043d", required = true)
    protected String наимРайон;

    /**
     * Gets the value of the типРайон property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getТипРайон() {
        return типРайон;
    }

    /**
     * Sets the value of the типРайон property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setТипРайон(String value) {
        this.типРайон = value;
    }

    /**
     * Gets the value of the наимРайон property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНаимРайон() {
        return наимРайон;
    }

    /**
     * Sets the value of the наимРайон property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНаимРайон(String value) {
        this.наимРайон = value;
    }

}
