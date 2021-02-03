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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения об аккредитации представительства или филиала иностранной организации в Российской Федерации
 * 
 * <p>Java class for СвАкРАФПТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="СвАкРАФПТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ГРНДата" type="{}ГРНДатаТип"/&gt;
 *         &lt;element name="ГРНДатаИспр" type="{}ГРНДатаТип" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="НомерРАФП" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="11"/&gt;
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
@XmlType(name = "\u0421\u0432\u0410\u043a\u0420\u0410\u0424\u041f\u0422\u0438\u043f", propOrder = {
    "\u0433\u0440\u043d\u0414\u0430\u0442\u0430",
    "\u0433\u0440\u043d\u0414\u0430\u0442\u0430\u0418\u0441\u043f\u0440"
})
public class СвАкРАФПТип {

    @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430", required = true)
    protected ГРНДатаТип грнДата;
    @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430\u0418\u0441\u043f\u0440")
    protected ГРНДатаТип грнДатаИспр;
    @XmlAttribute(name = "\u041d\u043e\u043c\u0435\u0440\u0420\u0410\u0424\u041f", required = true)
    protected String номерРАФП;

    /**
     * Gets the value of the грнДата property.
     * 
     * @return
     *     possible object is
     *     {@link ГРНДатаТип }
     *     
     */
    public ГРНДатаТип getГРНДата() {
        return грнДата;
    }

    /**
     * Sets the value of the грнДата property.
     * 
     * @param value
     *     allowed object is
     *     {@link ГРНДатаТип }
     *     
     */
    public void setГРНДата(ГРНДатаТип value) {
        this.грнДата = value;
    }

    /**
     * Gets the value of the грнДатаИспр property.
     * 
     * @return
     *     possible object is
     *     {@link ГРНДатаТип }
     *     
     */
    public ГРНДатаТип getГРНДатаИспр() {
        return грнДатаИспр;
    }

    /**
     * Sets the value of the грнДатаИспр property.
     * 
     * @param value
     *     allowed object is
     *     {@link ГРНДатаТип }
     *     
     */
    public void setГРНДатаИспр(ГРНДатаТип value) {
        this.грнДатаИспр = value;
    }

    /**
     * Gets the value of the номерРАФП property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getНомерРАФП() {
        return номерРАФП;
    }

    /**
     * Sets the value of the номерРАФП property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setНомерРАФП(String value) {
        this.номерРАФП = value;
    }

}
