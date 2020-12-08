//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.27 at 05:20:15 PM IRKT 
//


package ru.sibdigital.proccovid.dto.egrip;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ГРН и дата внесения записи в ЕГРИП
 * 
 * <p>Java class for ГРНИПДатаТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ГРНИПДатаТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="ГРНИП" type="{}ОГРНИПТип" /&gt;
 *       &lt;attribute name="ДатаЗаписи" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}date"&gt;
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
@XmlType(name = "\u0413\u0420\u041d\u0418\u041f\u0414\u0430\u0442\u0430\u0422\u0438\u043f")
public class ГРНИПДатаТип {

    @XmlAttribute(name = "\u0413\u0420\u041d\u0418\u041f")
    protected String грнип;
    @XmlAttribute(name = "\u0414\u0430\u0442\u0430\u0417\u0430\u043f\u0438\u0441\u0438", required = true)
    protected XMLGregorianCalendar датаЗаписи;

    /**
     * Gets the value of the грнип property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getГРНИП() {
        return грнип;
    }

    /**
     * Sets the value of the грнип property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setГРНИП(String value) {
        this.грнип = value;
    }

    /**
     * Gets the value of the датаЗаписи property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getДатаЗаписи() {
        return датаЗаписи;
    }

    /**
     * Sets the value of the датаЗаписи property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setДатаЗаписи(XMLGregorianCalendar value) {
        this.датаЗаписи = value;
    }

}
