//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.27 at 05:01:36 PM IRKT 
//


package ru.sibdigital.proccovid.dto.egrul;

import javax.xml.bind.annotation.*;


/**
 * Сведения об обременении доли участника, внесенные в ЕГРЮЛ
 * 
 * <p>Java class for СвОбремТип complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="СвОбремТип"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="РешСуд" type="{}РешСудТип" minOccurs="0"/&gt;
 *         &lt;element name="ГРНДата" type="{}ГРНДатаТип"/&gt;
 *         &lt;element name="ГРНДатаИспр" type="{}ГРНДатаТип" minOccurs="0"/&gt;
 *         &lt;element name="СвЗалогДержФЛ" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ГРНДатаПерв" type="{}ГРНДатаТип" minOccurs="0"/&gt;
 *                   &lt;element name="СвФЛ" type="{}СвФЛЕГРЮЛТип"/&gt;
 *                   &lt;element name="СвНотУдДогЗал" type="{}СвНотУдДогЗалТип" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="СвЗалогДержЮЛ" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ГРНДатаПерв" type="{}ГРНДатаТип" minOccurs="0"/&gt;
 *                   &lt;element name="НаимИННЮЛ" type="{}СвЮЛЕГРЮЛТип"/&gt;
 *                   &lt;element name="СвРегИн" type="{}СвРегИнЮЛЕГРЮЛТип" minOccurs="0"/&gt;
 *                   &lt;element name="СвНотУдДогЗал" type="{}СвНотУдДогЗалТип" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ВидОбрем" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="5"/&gt;
 *             &lt;maxLength value="16"/&gt;
 *             &lt;enumeration value="ЗАЛОГ"/&gt;
 *             &lt;enumeration value="ИНОЕ ОБРЕМЕНЕНИЕ"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="СрокОбременения"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="5000"/&gt;
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
@XmlType(name = "\u0421\u0432\u041e\u0431\u0440\u0435\u043c\u0422\u0438\u043f", propOrder = {
    "\u0440\u0435\u0448\u0421\u0443\u0434",
    "\u0433\u0440\u043d\u0414\u0430\u0442\u0430",
    "\u0433\u0440\u043d\u0414\u0430\u0442\u0430\u0418\u0441\u043f\u0440",
    "\u0441\u0432\u0417\u0430\u043b\u043e\u0433\u0414\u0435\u0440\u0436\u0424\u041b",
    "\u0441\u0432\u0417\u0430\u043b\u043e\u0433\u0414\u0435\u0440\u0436\u042e\u041b"
})
public class СвОбремТип {

    @XmlElement(name = "\u0420\u0435\u0448\u0421\u0443\u0434")
    protected РешСудТип решСуд;
    @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430", required = true)
    protected ГРНДатаТип грнДата;
    @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430\u0418\u0441\u043f\u0440")
    protected ГРНДатаТип грнДатаИспр;
    @XmlElement(name = "\u0421\u0432\u0417\u0430\u043b\u043e\u0433\u0414\u0435\u0440\u0436\u0424\u041b")
    protected СвЗалогДержФЛ свЗалогДержФЛ;
    @XmlElement(name = "\u0421\u0432\u0417\u0430\u043b\u043e\u0433\u0414\u0435\u0440\u0436\u042e\u041b")
    protected СвЗалогДержЮЛ свЗалогДержЮЛ;
    @XmlAttribute(name = "\u0412\u0438\u0434\u041e\u0431\u0440\u0435\u043c", required = true)
    protected String видОбрем;
    @XmlAttribute(name = "\u0421\u0440\u043e\u043a\u041e\u0431\u0440\u0435\u043c\u0435\u043d\u0435\u043d\u0438\u044f")
    protected String срокОбременения;

    /**
     * Gets the value of the решСуд property.
     *
     * @return
     *     possible object is
     *     {@link РешСудТип }
     *
     */
    public РешСудТип getРешСуд() {
        return решСуд;
    }

    /**
     * Sets the value of the решСуд property.
     *
     * @param value
     *     allowed object is
     *     {@link РешСудТип }
     *
     */
    public void setРешСуд(РешСудТип value) {
        this.решСуд = value;
    }

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
     * Gets the value of the свЗалогДержФЛ property.
     *
     * @return
     *     possible object is
     *     {@link СвЗалогДержФЛ }
     *
     */
    public СвЗалогДержФЛ getСвЗалогДержФЛ() {
        return свЗалогДержФЛ;
    }

    /**
     * Sets the value of the свЗалогДержФЛ property.
     *
     * @param value
     *     allowed object is
     *     {@link СвЗалогДержФЛ }
     *
     */
    public void setСвЗалогДержФЛ(СвЗалогДержФЛ value) {
        this.свЗалогДержФЛ = value;
    }

    /**
     * Gets the value of the свЗалогДержЮЛ property.
     *
     * @return
     *     possible object is
     *     {@link СвЗалогДержЮЛ }
     *
     */
    public СвЗалогДержЮЛ getСвЗалогДержЮЛ() {
        return свЗалогДержЮЛ;
    }

    /**
     * Sets the value of the свЗалогДержЮЛ property.
     *
     * @param value
     *     allowed object is
     *     {@link СвЗалогДержЮЛ }
     *
     */
    public void setСвЗалогДержЮЛ(СвЗалогДержЮЛ value) {
        this.свЗалогДержЮЛ = value;
    }

    /**
     * Gets the value of the видОбрем property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getВидОбрем() {
        return видОбрем;
    }

    /**
     * Sets the value of the видОбрем property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setВидОбрем(String value) {
        this.видОбрем = value;
    }

    /**
     * Gets the value of the срокОбременения property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getСрокОбременения() {
        return срокОбременения;
    }

    /**
     * Sets the value of the срокОбременения property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setСрокОбременения(String value) {
        this.срокОбременения = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="ГРНДатаПерв" type="{}ГРНДатаТип" minOccurs="0"/&gt;
     *         &lt;element name="СвФЛ" type="{}СвФЛЕГРЮЛТип"/&gt;
     *         &lt;element name="СвНотУдДогЗал" type="{}СвНотУдДогЗалТип" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "\u0433\u0440\u043d\u0414\u0430\u0442\u0430\u041f\u0435\u0440\u0432",
        "\u0441\u0432\u0424\u041b",
        "\u0441\u0432\u041d\u043e\u0442\u0423\u0434\u0414\u043e\u0433\u0417\u0430\u043b"
    })
    public static class СвЗалогДержФЛ {

        @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430\u041f\u0435\u0440\u0432")
        protected ГРНДатаТип грнДатаПерв;
        @XmlElement(name = "\u0421\u0432\u0424\u041b", required = true)
        protected СвФЛЕГРЮЛТип свФЛ;
        @XmlElement(name = "\u0421\u0432\u041d\u043e\u0442\u0423\u0434\u0414\u043e\u0433\u0417\u0430\u043b")
        protected СвНотУдДогЗалТип свНотУдДогЗал;

        /**
         * Gets the value of the грнДатаПерв property.
         * 
         * @return
         *     possible object is
         *     {@link ГРНДатаТип }
         *     
         */
        public ГРНДатаТип getГРНДатаПерв() {
            return грнДатаПерв;
        }

        /**
         * Sets the value of the грнДатаПерв property.
         * 
         * @param value
         *     allowed object is
         *     {@link ГРНДатаТип }
         *     
         */
        public void setГРНДатаПерв(ГРНДатаТип value) {
            this.грнДатаПерв = value;
        }

        /**
         * Gets the value of the свФЛ property.
         * 
         * @return
         *     possible object is
         *     {@link СвФЛЕГРЮЛТип }
         *     
         */
        public СвФЛЕГРЮЛТип getСвФЛ() {
            return свФЛ;
        }

        /**
         * Sets the value of the свФЛ property.
         * 
         * @param value
         *     allowed object is
         *     {@link СвФЛЕГРЮЛТип }
         *     
         */
        public void setСвФЛ(СвФЛЕГРЮЛТип value) {
            this.свФЛ = value;
        }

        /**
         * Gets the value of the свНотУдДогЗал property.
         * 
         * @return
         *     possible object is
         *     {@link СвНотУдДогЗалТип }
         *     
         */
        public СвНотУдДогЗалТип getСвНотУдДогЗал() {
            return свНотУдДогЗал;
        }

        /**
         * Sets the value of the свНотУдДогЗал property.
         * 
         * @param value
         *     allowed object is
         *     {@link СвНотУдДогЗалТип }
         *     
         */
        public void setСвНотУдДогЗал(СвНотУдДогЗалТип value) {
            this.свНотУдДогЗал = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="ГРНДатаПерв" type="{}ГРНДатаТип" minOccurs="0"/&gt;
     *         &lt;element name="НаимИННЮЛ" type="{}СвЮЛЕГРЮЛТип"/&gt;
     *         &lt;element name="СвРегИн" type="{}СвРегИнЮЛЕГРЮЛТип" minOccurs="0"/&gt;
     *         &lt;element name="СвНотУдДогЗал" type="{}СвНотУдДогЗалТип" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "\u0433\u0440\u043d\u0414\u0430\u0442\u0430\u041f\u0435\u0440\u0432",
        "\u043d\u0430\u0438\u043c\u0418\u041d\u041d\u042e\u041b",
        "\u0441\u0432\u0420\u0435\u0433\u0418\u043d",
        "\u0441\u0432\u041d\u043e\u0442\u0423\u0434\u0414\u043e\u0433\u0417\u0430\u043b"
    })
    public static class СвЗалогДержЮЛ {

        @XmlElement(name = "\u0413\u0420\u041d\u0414\u0430\u0442\u0430\u041f\u0435\u0440\u0432")
        protected ГРНДатаТип грнДатаПерв;
        @XmlElement(name = "\u041d\u0430\u0438\u043c\u0418\u041d\u041d\u042e\u041b", required = true)
        protected СвЮЛЕГРЮЛТип наимИННЮЛ;
        @XmlElement(name = "\u0421\u0432\u0420\u0435\u0433\u0418\u043d")
        protected СвРегИнЮЛЕГРЮЛТип свРегИн;
        @XmlElement(name = "\u0421\u0432\u041d\u043e\u0442\u0423\u0434\u0414\u043e\u0433\u0417\u0430\u043b")
        protected СвНотУдДогЗалТип свНотУдДогЗал;

        /**
         * Gets the value of the грнДатаПерв property.
         * 
         * @return
         *     possible object is
         *     {@link ГРНДатаТип }
         *     
         */
        public ГРНДатаТип getГРНДатаПерв() {
            return грнДатаПерв;
        }

        /**
         * Sets the value of the грнДатаПерв property.
         * 
         * @param value
         *     allowed object is
         *     {@link ГРНДатаТип }
         *     
         */
        public void setГРНДатаПерв(ГРНДатаТип value) {
            this.грнДатаПерв = value;
        }

        /**
         * Gets the value of the наимИННЮЛ property.
         * 
         * @return
         *     possible object is
         *     {@link СвЮЛЕГРЮЛТип }
         *     
         */
        public СвЮЛЕГРЮЛТип getНаимИННЮЛ() {
            return наимИННЮЛ;
        }

        /**
         * Sets the value of the наимИННЮЛ property.
         * 
         * @param value
         *     allowed object is
         *     {@link СвЮЛЕГРЮЛТип }
         *     
         */
        public void setНаимИННЮЛ(СвЮЛЕГРЮЛТип value) {
            this.наимИННЮЛ = value;
        }

        /**
         * Gets the value of the свРегИн property.
         * 
         * @return
         *     possible object is
         *     {@link СвРегИнЮЛЕГРЮЛТип }
         *     
         */
        public СвРегИнЮЛЕГРЮЛТип getСвРегИн() {
            return свРегИн;
        }

        /**
         * Sets the value of the свРегИн property.
         * 
         * @param value
         *     allowed object is
         *     {@link СвРегИнЮЛЕГРЮЛТип }
         *     
         */
        public void setСвРегИн(СвРегИнЮЛЕГРЮЛТип value) {
            this.свРегИн = value;
        }

        /**
         * Gets the value of the свНотУдДогЗал property.
         * 
         * @return
         *     possible object is
         *     {@link СвНотУдДогЗалТип }
         *     
         */
        public СвНотУдДогЗалТип getСвНотУдДогЗал() {
            return свНотУдДогЗал;
        }

        /**
         * Sets the value of the свНотУдДогЗал property.
         * 
         * @param value
         *     allowed object is
         *     {@link СвНотУдДогЗалТип }
         *     
         */
        public void setСвНотУдДогЗал(СвНотУдДогЗалТип value) {
            this.свНотУдДогЗал = value;
        }

    }

}
