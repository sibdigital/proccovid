package ru.sibdigital.proccovid.utils;

import ru.sibdigital.proccovid.dto.egrip.EGRIP;
import ru.sibdigital.proccovid.dto.egrul.*;

public class JuridicalUtils {

    public static String constructJuridicalAdress(EGRUL.СвЮЛ sved){
        String result = "";
        final EGRUL.СвЮЛ.СвАдресЮЛ adr = sved.getСвАдресЮЛ();
        if (adr != null) {
            result = constructAddress(adr.getАдресРФ());
        }
        return result;
    }

    public static String constructAddress(АдрРФЕГРЮЛТип adrrf) {
        String result = "";
        if (adrrf != null) {
            final РегионТип region = adrrf.getРегион();
            final ГородТип gorod = adrrf.getГород();
            final String index = adrrf.getИндекс() != null ? adrrf.getИндекс() : "";
            final РайонТип raion = adrrf.getРайон();
            final НаселПунктТип naspunkt = adrrf.getНаселПункт();
            final УлицаТип ulica = adrrf.getУлица();
            final String dom = adrrf.getДом() != null ? adrrf.getДом() : "";
            final String kvart = adrrf.getКварт() != null ? adrrf.getКварт() : "";
            final String korpus = adrrf.getКорпус() != null ? adrrf.getКорпус() : "";

            String regionFormat = region != null ? (region.getТипРегион() + " " + region.getНаимРегион()) : "";
            String gorodFormat = gorod != null ? (gorod.getТипГород() + " " + gorod.getНаимГород()) : "";
            String raionFormat = raion != null ? (raion.getТипРайон() + " " + raion.getНаимРайон()) : "";
            String naspunktFormat = naspunkt != null ? (naspunkt.getТипНаселПункт() + " " + naspunkt.getНаимНаселПункт()) : "";
            String ulicaFormat = ulica != null ? (ulica.getТипУлица() + " " + ulica.getНаимУлица()) : "";

            result = index + ", " + regionFormat + ", " + gorodFormat + ", " + raionFormat + ", " + naspunktFormat
                    + ", " + ulicaFormat + ", " + dom + ", " + korpus + ", " + kvart;
        }
        return result;
    }

    public static String constructJuridicalAdress(EGRIP.СвИП sved){
        String result = "";
        final EGRIP.СвИП.СвАдрМЖ adr = sved.getСвАдрМЖ();
        if (adr != null) {
            final EGRIP.СвИП.СвАдрМЖ.АдресРФ adrrf = adr.getАдресРФ();
            if (adrrf != null) {
                final ru.sibdigital.proccovid.dto.egrip.РегионТип region = adrrf.getРегион();
                final ru.sibdigital.proccovid.dto.egrip.ГородТип gorod = adrrf.getГород();
                final ru.sibdigital.proccovid.dto.egrip.РайонТип raion = adrrf.getРайон();
                final ru.sibdigital.proccovid.dto.egrip.НаселПунктТип naspunkt = adrrf.getНаселПункт();

                String regionFormat = region != null ? (region.getТипРегион() + " " + region.getНаимРегион()) : "";
                String gorodFormat = gorod != null ? (gorod.getТипГород() + " " + gorod.getНаимГород()) : "";
                String raionFormat = raion != null ? (raion.getТипРайон() + " " + raion.getНаимРайон()) : "";
                String naspunktFormat = naspunkt != null ? (naspunkt.getТипНаселПункт() + " " + naspunkt.getНаимНаселПункт()) : "";

                result = regionFormat + ", " + gorodFormat + ", " + raionFormat + ", " + naspunktFormat;
            }
        }
        return result;
    }
}
