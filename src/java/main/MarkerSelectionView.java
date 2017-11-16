package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import arret.Station;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import stationdetail.Carto;

/**
 *
 * @author geoffreydalfin
 */
@ManagedBean
@ViewScoped
public class MarkerSelectionView implements Serializable {

    /**
     * Creates a new instance of InfoWindowsViewBean
     */


    private MapModel simpleModel;

    private Marker marker;
    private Station station;

    @PostConstruct
    public void init() {
        simpleModel = new DefaultMapModel();

        try {
            JAXBContext JC = JAXBContext.newInstance("stationdetail");

            //Unmarshaller unmarshaller = JC.createUnmarshaller();
            URL url = new URL("http://www.velib.paris/service/carto");
            Carto mCarto = (Carto) JC.createUnmarshaller().unmarshal(url);
            List<stationdetail.Marker> markers = mCarto.getMarkers().getMarker();
            for (int i = 0; i < markers.size(); i++) {
                LatLng coord = new LatLng(markers.get(i).getLat().doubleValue(), markers.get(i).getLng().doubleValue());
                simpleModel.addOverlay(new Marker(coord,markers.get(i).getName(), markers.get(i).getNumber()));
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(MarkerSelectionView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(MarkerSelectionView.class.getName()).log(Level.SEVERE, null, ex);
        } 

        //Icons and Data
    }

    public MapModel getSimpleModel() {
        return simpleModel;
    }

    public void onMarkerSelect(OverlaySelectEvent event)
    {
       marker = (Marker) event.getOverlay();
      
        try {
             JAXBContext JC1 = JAXBContext.newInstance("arret");

            //Unmarshaller unmarshaller = JC.createUnmarshaller();
            URL url = new URL("http://www.velib.paris/service/stationdetails/"+ marker.getData().toString());
            Station mStation = (Station) JC1.createUnmarshaller().unmarshal(url);
            
            String details = marker.getTitle();
            details += "Updated : "+ mStation.getUpdated();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, marker.getTitle(), details));
            
        } catch (JAXBException ex) {
            Logger.getLogger(MarkerSelectionView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MarkerSelectionView.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
       
    }

    public Marker getMarker() {
        return marker;
    }
}

