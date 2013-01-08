package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.common.core.types.StringArray;
import org.gcube.common.gis.datamodel.utils.Utils;
import org.gcube_system.namespaces.application.aquamaps.gistypes.WMSContextInfoType;

public class WMSContextInfo {

	private int width;
	private int height;
	private String displayProjection;
	private BoundsInfo maxExtent;
	private BoundsInfo minExtent;
	private int numZoomLevels;
	private int zoomTo;
	private double lon_center;
	private double lat_center;
	private String units;
	private String title;
	private String name;
	private double maxResolution;
	private ArrayList<String> layers;
	private ArrayList<String> keywords;
	private String _abstract;
	private String logoFormat;
	private int logoWidth;
	private int logoHeight;
	private String logoUrl;
	private String contactInformation;
	
	
	public WMSContextInfo(WMSContextInfoType toLoad) {
		super();
		
		
		this.setWidth(toLoad.getWidth());
		this.setHeight(toLoad.getHeight());
		this.setDisplayProjection(toLoad.getDisplayProjection());
		if(toLoad.getMaxExtent() != null) this.setMaxExtent(new BoundsInfo(toLoad.getMaxExtent()));
		if(toLoad.getMinExtent() != null) this.setMinExtent(new BoundsInfo(toLoad.getMinExtent()));
		this.setNumZoomLevels(toLoad.getNumZoomLevels());
		this.setZoomTo(toLoad.getZoomTo());
		this.setLon_center(toLoad.getLon_center());
		this.setLat_center(toLoad.getLat_center());
		this.setUnits(toLoad.getUnits());
		this.setTitle(toLoad.getTitle());
		this.setName(toLoad.getName());
		this.setMaxResolution(toLoad.getMaxResolution());
		if (toLoad.getLayers() != null) 
			for(String layer:toLoad.getLayers().getItems())layers.add(layer);
		if (toLoad.getKeywords() != null) this.setKeywords(Utils.loadString(toLoad.getKeywords()));
		this.set_abstract(toLoad.get_abstract());
		this.setLogoFormat(toLoad.getLogoFormat());
		this.setLogoHeight(toLoad.getLogoHeight());
		this.setLogoWidth(toLoad.getLogoWidth());
		this.setLogoUrl(toLoad.getLogoUrl());
		this.setContactInformation(toLoad.getContactInformation());
	}
	
	public WMSContextInfoType toStubsVersion() {
		WMSContextInfoType res = new WMSContextInfoType();
		
	
		res.setWidth(this.getWidth());
		res.setHeight(this.getHeight());
		res.setDisplayProjection(this.getDisplayProjection());
		if (this.getMaxExtent() != null) res.setMaxExtent(this.getMaxExtent().toStubsVersion());
		if (this.getMinExtent() != null) res.setMinExtent(this.getMinExtent().toStubsVersion());
		res.setNumZoomLevels(this.getNumZoomLevels());
		res.setZoomTo(this.getZoomTo());
		res.setLon_center(this.getLon_center());
		res.setLat_center(this.getLat_center());
		res.setUnits(this.getUnits());
		res.setTitle(this.getTitle());
		res.setName(this.getName());
		res.setMaxResolution(this.getMaxResolution());
		if (this.getLayers() != null) res.setLayers(new StringArray(getLayers().toArray(new String[getLayers().size()])));
		if (this.getKeywords() != null) res.setKeywords(new StringArray(this.getKeywords().toArray(new String[this.getKeywords().size()])));
		res.set_abstract(this.get_abstract());
		res.setContactInformation(this.getContactInformation());
		res.setLogoFormat(this.getLogoFormat());
		res.setLogoHeight(this.getLogoHeight());
		res.setLogoUrl(this.getLogoUrl());
		res.setLogoWidth(this.getLogoWidth());
				
		return res;
	}
	
	

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String get_abstract() {
		return _abstract;
	}

	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}

	public String getDisplayProjection() {
		return displayProjection;
	}

	public void setDisplayProjection(String displayProjection) {
		this.displayProjection = displayProjection;
	}

	public int getNumZoomLevels() {
		return numZoomLevels;
	}

	public void setNumZoomLevels(int numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}

	public int getZoomTo() {
		return zoomTo;
	}

	public void setZoomTo(int zoomTo) {
		this.zoomTo = zoomTo;
	}

	public double getLon_center() {
		return lon_center;
	}

	public void setLon_center(double lon_center) {
		this.lon_center = lon_center;
	}

	public double getLat_center() {
		return lat_center;
	}

	public void setLat_center(double lat_center) {
		this.lat_center = lat_center;
	}

	public double getMaxResolution() {
		return maxResolution;
	}

	public void setMaxResolution(double maxResolution) {
		this.maxResolution = maxResolution;
	}

	public ArrayList<String> getLayers() {
		return layers;
	}
	public void setLayers(ArrayList<String> layers) {
		this.layers = layers;
	}

	public BoundsInfo getMaxExtent() {
		return maxExtent;
	}

	public void setMaxExtent(BoundsInfo maxExtent) {
		this.maxExtent = maxExtent;
	}

	public BoundsInfo getMinExtent() {
		return minExtent;
	}

	public void setMinExtent(BoundsInfo minExtent) {
		this.minExtent = minExtent;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public WMSContextInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public String getLogoFormat() {
		return logoFormat;
	}

	public void setLogoFormat(String logoFormat) {
		this.logoFormat = logoFormat;
	}

	public int getLogoWidth() {
		return logoWidth;
	}

	public void setLogoWidth(int logoWidth) {
		this.logoWidth = logoWidth;
	}

	public int getLogoHeight() {
		return logoHeight;
	}

	public void setLogoHeight(int logoHeight) {
		this.logoHeight = logoHeight;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}
}