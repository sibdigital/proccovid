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
 * Сведения об адресообразующем элементе населенный пункт
 * 
 * <p>Java class for НаселПунктТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="НаселПунктТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="ТипНаселПункт"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="100"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="НаимНаселПункт" use="required"&gt;
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
@XmlType(name = "\u041d\u0430\u0441\u0435\u043b\u041f\u0443\u043d\u043a\u0442\u0422\u0438\u043f")
public class НаселПунктТип {

    @XmlAttribute(name = "\u0422\u0438\u043f\u041d\u0430\u0441\u0435\u043b\u041f\u0443\u043d\u043a\u0442")
    protected String типНаселПункт;
    @XmlAttribute(name = "\u041d\u0430\u0438\u043c\u041d\u0430\u0441\u0435\u043b\u041f\u0443\u043d\u043a\u0442", required = true)
    protected String наимНаселПункт;

    /**
     * Gets the value of the типНаселПункт property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getТипНаселПункт() {
        return типНаселПункт;
    }

    /**
     * Sets the value of the типНаселПункт property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setТипНаселПункт(String value) {
        this.типНаселПункт = value;
    }

    /**
     * Gets the value of the наимНаселПункт property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНаимНаселПункт() {
        return наимНаселПункт;
    }

    /**
     * Sets the value of the наимНаселПункт property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНаимНаселПункт(String value) {
        this.наимНаселПункт = value;
    }

}
