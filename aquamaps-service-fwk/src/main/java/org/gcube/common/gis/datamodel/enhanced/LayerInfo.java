package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.common.core.types.StringArray;
import org.gcube.common.gis.datamodel.types.LayersType;
import org.gcube.common.gis.datamodel.utils.Utils;
import org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.gistypes.LayerType;

public class LayerInfo {


	public LayerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String name;
	private String title;
	private String _abstract;
	private String url;
	private String serverProtocol;
	private String serverPassword;
	private String serverLogin;
	private String serverType;
	private String srs;
	private LayersType type;
	private boolean trasparent=false;
	private boolean baseLayer=false;
	private int buffer = 0;
	private boolean hasLegend=false;
	private boolean visible=false;
	private boolean selected=false;
	private boolean queryable=false;
	private BoundsInfo maxExtent;
	private BoundsInfo minExtent;
	private String defaultStyle;
	private double opacity=0.0;
	private ArrayList<String> styles;
	private TransectInfo transect;
	
	public LayerInfo(LayerInfoType toLoad){
		this.setName(toLoad.getName());
		this.setTitle(toLoad.getTitle());
		this.set_abstract(toLoad.get_abstract());
		this.setUrl(toLoad.getUrl());
		this.setServerLogin(toLoad.getServerLogin());
		this.setServerPassword(toLoad.getServerPassword());
		this.setServerProtocol(toLoad.getServerProtocol());
		this.setServerType(toLoad.getServerType());
		this.setSrs(toLoad.getSrs());
		if(toLoad.getType() != null && !toLoad.getType().getValue().contentEquals("")) this.setType(LayersType.valueOf(toLoad.getType().getValue()));
		this.setTrasparent(toLoad.isTrasparent());
		this.setBaseLayer(toLoad.isBaseLayer());
		this.setBuffer(toLoad.getBuffer());
		this.setHasLegend(toLoad.isHasLegend());
		this.setVisible(toLoad.isVisible());
		this.setSelected(toLoad.isSelected());
		this.setQueryable(toLoad.isQueryable());
		if(toLoad.getMaxExtent() != null) this.setMaxExtent(new BoundsInfo(toLoad.getMaxExtent()));
		if(toLoad.getMinExtent() != null) this.setMinExtent(new BoundsInfo(toLoad.getMinExtent()));
		this.setDefaultStyle(toLoad.getDefaultStyle());
		this.setOpacity(toLoad.getOpacity());
		if (toLoad.getStyles() != null) this.setStyles(Utils.loadString(toLoad.getStyles()));
		if (toLoad.getTransect() != null) this.setTransect(new TransectInfo(toLoad.getTransect()));
	}
	
	public LayerInfoType toStubsVersion(){
		LayerInfoType res = new LayerInfoType();
		res.setName(this.getName());
		res.setTitle(this.getTitle());
		res.set_abstract(this.get_abstract());
		res.setUrl(this.getUrl());
		res.setServerLogin(this.getServerLogin());
		res.setServerPassword(this.getServerPassword());
		res.setServerProtocol(this.getServerProtocol());
		res.setServerType(this.getServerType());
		res.setSrs(this.getSrs());
		if (this.getType() != null && !this.getType().name().contentEquals("")) res.setType(LayerType.fromString(this.getType().name()));
		res.setTrasparent(this.isTrasparent());
		res.setBaseLayer(this.isBaseLayer());
		res.setBuffer(this.getBuffer());
		res.setHasLegend(this.isHasLegend());
		res.setVisible(this.isVisible());
		res.setSelected(this.isSelected());
		res.setQueryable(this.isQueryable());
		if (this.getMaxExtent() != null) res.setMaxExtent(this.getMaxExtent().toStubsVersion());
		if (this.getMinExtent() != null) res.setMinExtent(this.getMinExtent().toStubsVersion());
		res.setDefaultStyle(this.getDefaultStyle());
		res.setOpacity(this.getOpacity());
		if (this.getStyles() != null) res.setStyles(new StringArray(this.getStyles().toArray(new String[this.getStyles().size()])));
		if (this.getTransect() != null) res.setTransect(this.getTransect().toStubsVersion());
			
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

	public String getServerLogin() {
		return serverLogin;
	}

	public void setServerLogin(String serverLogin) {
		this.serverLogin = serverLogin;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public LayersType getType() {
		return type;
	}

	public void setType(LayersType type) {
		this.type = type;
	}

	public boolean isTrasparent() {
		return trasparent;
	}

	public void setTrasparent(boolean trasparent) {
		this.trasparent = trasparent;
	}

	public boolean isBaseLayer() {
		return baseLayer;
	}

	public void setBaseLayer(boolean baseLayer) {
		this.baseLayer = baseLayer;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public boolean isHasLegend() {
		return hasLegend;
	}

	public void setHasLegend(boolean hasLegend) {
		this.hasLegend = hasLegend;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isQueryable() {
		return queryable;
	}

	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
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

	public String getDefaultStyle() {
		return defaultStyle;
	}

	public void setDefaultStyle(String defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	public ArrayList<String> getStyles() {
		return styles;
	}

	public void setStyles(ArrayList<String> styles) {
		this.styles = styles;
	}

	public TransectInfo getTransect() {
		return transect;
	}

	public void setTransect(TransectInfo transect) {
		this.transect = transect;
	}
}
