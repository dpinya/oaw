package es.inteco.rastreador2.utils;

import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.rastreador2.actionform.rastreo.FulfilledCrawlingForm;
import es.inteco.rastreador2.dao.cartucho.CartuchoDAO;
import es.inteco.rastreador2.dao.rastreo.RastreoDAO;
import es.inteco.utils.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static es.inteco.common.Constants.CRAWLER_PROPERTIES;

public final class RastreoUtils {

    private RastreoUtils() {
    }

    public static void borrarArchivosAsociados(final Connection c, final String idRastreo) throws SQLException {
        final PropertiesManager pmgr = new PropertiesManager();
        final FulfilledCrawlingForm fulfilledCrawlingForm = RastreoDAO.getFullfilledCrawlingExecution(c, Long.parseLong(idRastreo));
        final int cartucho = Integer.parseInt(fulfilledCrawlingForm.getIdCartridge());
        final int id = Integer.parseInt(fulfilledCrawlingForm.getId());
        String pdfPath = "";

        if (CartuchoDAO.isCartuchoAccesibilidad(c, cartucho)) {
            String chartPath = pmgr.getValue(CRAWLER_PROPERTIES, "path.general.intav.chart.files") + File.separator + idRastreo + File.separator + id;
            pdfPath = pmgr.getValue(CRAWLER_PROPERTIES, "path.inteco.exports.intav");
            FileUtils.deleteDir(new File(chartPath));
            File rastreoDir = new File(pmgr.getValue(CRAWLER_PROPERTIES, "path.general.intav.chart.files") + File.separator + idRastreo);
            if (existenGraficasAsociadas(rastreoDir)) {
                if (!rastreoDir.delete()) {
                    Logger.putLog("No se ha podido borrar el directorio temporal del rastreo " + idRastreo, RastreoUtils.class, Logger.LOG_LEVEL_ERROR);
                }
            }
        } else if (cartucho == Integer.parseInt(pmgr.getValue(CRAWLER_PROPERTIES, "cartridge.lenox.id"))) {
            pdfPath = pmgr.getValue(CRAWLER_PROPERTIES, "path.inteco.exports.lenox");
        }

        FileUtils.deleteDir(new File(pdfPath + File.separator + idRastreo + File.separator + id));
        File rastreoDir = new File(pdfPath + File.separator + idRastreo);
        if (!rastreoDir.delete()) {
            Logger.putLog("No se ha podido borrar el directorio temporal del rastreo " + idRastreo, RastreoUtils.class, Logger.LOG_LEVEL_ERROR);
        }
    }

    private static boolean existenGraficasAsociadas(final File rastreoDir) {
        if (rastreoDir.isDirectory()) {
            final String[] ficheros = rastreoDir.list();
            return ficheros != null && ficheros.length == 0;
        } else {
            return false;
        }
    }

}