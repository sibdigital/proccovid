//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.12.26 at 02:54:32 PM IRKT 
//


package ru.sibdigital.proccovid.dto.egrip040501;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения об адресообразующем элементе город
 * 
 * <p>Java class for ГородТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ГородТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="ТипГород" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="100"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="НаимГород" use="required"&gt;
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
@XmlType(name = "\u0413\u043e\u0440\u043e\u0434\u0422\u0438\u043f")
public class ГородТип {

    @XmlAttribute(name = "\u0422\u0438\u043f\u0413\u043e\u0440\u043e\u0434", required = true)
    protected String типГород;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u0413\u043e\u0440\u043e\u0434", required = true)
    protected String наимГород;

    /**
     * Gets the value of the типГород property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getТипГород() {
        return типГород;
    }

    /**
     * Sets the value of the типГород property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setТипГород(String value) {
        this.типГород = value;
    }

    /**
     * Gets the value of the наимГород property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНаимГород() {
        return наимГород;
    }

    /**
     * Sets the value of the наимГород property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНаимГород(String value) {
        this.наимГород = value;
    }

}
