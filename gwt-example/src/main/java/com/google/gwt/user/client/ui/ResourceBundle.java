package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

interface ResourceBundle extends ClientBundle {

    static final ResourceBundle RES = GWT.create(ResourceBundle.class);
    //static final Messages message = (Messages) GWT.create(Messages.class);
    //static final GWTServiceAsync asyncService = GWT.create(GWTService.class);

    @Source("DesktopStyle.css")
    @CssResource.NotStrict
    DesktopStyle css();
//    @Source("publicfiles/css/CSC.css")
//    @CssResource.NotStrict
//    CssResource cscCss();
//
    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/hborder.png")
    DataResource rImagesHborderPng();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/vborder.png")
    DataResource rImagesVborderPng();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/corner.png")
    DataResource rImagesCornerPng();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/corner_ie6.png")
    DataResource rImagesCornerIe6Png();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/hborder_ie6.png")
    DataResource rImagesHborderIe6Png();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/vborder_ie6.png")
    DataResource rImagesVborderIe6Png();

    @Source("com/google/gwt/user/theme/standard/public/gwt/standard/images/splitPanelThumb.png")
    DataResource rImagesSplitPanelThumbPng();
}
