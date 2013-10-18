package net.jeeeyul.eclipse.themes.css

import net.jeeeyul.eclipse.themes.preference.ChromeThemeConfig
import org.eclipse.swt.graphics.RGB
import net.jeeeyul.eclipse.themes.preference.IChromeThemeConfig
import net.jeeeyul.eclipse.themes.Nature
import net.jeeeyul.eclipse.themes.ui.HSB

class ChromeCSSGenerator {
	extension Nature = Nature::INSTANCE

	IChromeThemeConfig config = ChromeThemeConfig::instance

	def setConfig(IChromeThemeConfig config) {
		this.config = config
	}

	def generate() '''
		/*
		 *  Chrome Theme generate css dynamically, So do not inspect this file, See "ChromeCSSGenerator.xtend" instead
		 */
		.jeeeyul-chrome-theme{
			/*
			 * This selector rule is exist for detect Chrome Theme to find rewrite target. 
			 * See "RewriteChormeCSS.java"
			 */	
		}
		
		.MTrimmedWindow.topLevel {
			margin-top: «config.sashWidth + if(config.usePartShadow) 3 else 0»px;
			margin-bottom: 2px;
			margin-left: 2px;
			margin-right: 2px;
		}
		
		.MPartStack {
			font-size: «config.partFontData.height as int»;
			font-family: '«config.partFontData.getName()»';
			swt-simple: true;
			swt-tab-renderer:
				url('bundleclass://net.jeeeyul.eclipse.themes/net.jeeeyul.eclipse.themes.rendering.ChromeTabRendering');
		
			padding: «config.partStackPadding»px «config.partStackPadding + 5»px «config.partStackPadding + 7»px «config.partStackPadding + 5»px; /* top left bottom right */
			swt-tab-outline: «config.inactiveOulineColor.toHtmlColor»;
			swt-outer-keyline-color: «config.inactiveOulineColor.toHtmlColor»;
			swt-unselected-tabs-color: «config.inactivePartGradientStart.toHtmlColor» «config.inactivePartGradientEnd.toHtmlColor» «config.inactiveSelectedTabEndColor.toHtmlColor» 99% 100%;
			swt-shadow-visible: «config.usePartShadow»;
			
			swt-selected-tab-fill: «config.inactiveSelectedTabEndColor.toHtmlColor»;
			chrome-selected-tab-fill-highlight: «config.inactiveSelectedTabStartColor.toHtmlColor»;
			
			chrome-selected-tab-color: «config.inactiveSelectedTitleColor.toHtmlColor»;
			chrome-unselected-tab-color: «config.inactiveUnselectedTitleColor.toHtmlColor»;
			
			swt-shadow-color: «config.partShadowColor.toHtmlColor»;
			
			chrome-shiney-shadow: «config.useInactivePartTitleShadow»;
			swt-mru-visible: «config.mruVisible»;
			
			swt-corner-radius: «config.partStackCornerRadius»px;
		}
		
		.MPartStack.active {
			swt-inner-keyline-color: #FFFFFF;
			swt-tab-outline: «config.activeOulineColor.toHtmlColor»;
			swt-outer-keyline-color: «config.activeOulineColor.toHtmlColor»;
			swt-unselected-tabs-color: «config.activePartGradientStart.toHtmlColor» «config.activePartGradientEnd.toHtmlColor» «config.activeSelectedTabEndColor.toHtmlColor» 99% 100%;
			
			swt-selected-tab-fill: «config.activeSelectedTabEndColor.toHtmlColor»;
			chrome-selected-tab-fill-highlight: «config.activeSelectedTabStartColor.toHtmlColor»;
			
			chrome-selected-tab-color: «config.activeSelectedTitleColor.toHtmlColor»;
			chrome-unselected-tab-color: «config.activeUnselectedTitleColor.toHtmlColor»;
			chrome-shiney-shadow: «config.useActivePartTitleShadow»;
		}
		
		.MPartStack.empty {
			swt-unselected-tabs-color: «config.emptyPartBackgroundColor.toHtmlColor» «config.emptyPartBackgroundColor.toHtmlColor» «config.emptyPartBackgroundColor.toHtmlColor» 99% 100%;
			swt-tab-outline: «config.emptyPartOutloneColor.toHtmlColor»;
			swt-outer-keyline-color: «config.emptyPartOutloneColor.toHtmlColor»;
		}
		
		.MTrimmedWindow {
		  	margin-top: 2px;
			margin-bottom: 2px;
			«IF config.usePartShadow»
				margin-left: 0px;
				margin-right: 0px;
			«ELSE»
				margin-left: 2px;
				margin-right: 2px;
			«ENDIF»
			background-color: «config.windowBackgroundColor.toHtmlColor»;
		}
		
		.MTrimBar {
			background-color: «config.windowBackgroundColor.toHtmlColor»;
		}
		
		.MTrimBar#org-eclipse-ui-main-toolbar {
			background-color: «config.toolbarGradientStart.toHtmlColor» «config.toolbarGradientEnd.toHtmlColor»;
		}
		
		CTabFolder.MArea .MPartStack,CTabFolder.MArea .MPartStack.active {
			swt-shadow-visible: false;
		}
		
		CTabFolder Canvas {
			background-color: #F8F8F8;
		}
		
		#org-eclipse-ui-editorss {
			swt-tab-renderer: url('bundleclass://org.eclipse.e4.ui.workbench.renderers.swt/org.eclipse.e4.ui.workbench.renderers.swt.CTabRendering');
			swt-unselected-tabs-color: #F0F0F0 #F0F0F0 #F0F0F0 100% 100%;
			swt-outer-keyline-color: #B4B4B4;
			swt-inner-keyline-color: #F0F0F0;
			swt-tab-outline: #F0F0F0;
			color: #F0F0F0;
			swt-tab-height: 8px;
			padding: 0px 5px 7px;
		}
		
		#org-eclipse-ui-trim-status{
			chrome-border-top-visible: «config.useStatusBarOutline»;
			«IF config.useStatusBarOutline»
				chrome-border-top-color: «config.statusBarOutlineColor.toHtmlColor»;
				chrome-padding-top: 1;
			«ELSE»
				chrome-padding-top: 0;
			«ENDIF»
			«IF !config.useWindowBackgroundAsStatusBarBackground»
				background-color: «config.statusBarBackgroundColor.toHtmlColor»;
			«ENDIF»
		}
		
		.MToolControl.TrimStack {
			«IF config.useTrimStackImageBorder»
				«IF version.isAfter(JUNO_SR1_RANGE)»
					frame-image: url(chrome://frame?background-color=«config.windowBackgroundColor.toHSB.toHTMLCode»);
				«ELSE»
					frame-image: url(images/frame.png);
				«ENDIF»
				frame-cuts: 5px 1px 5px 16px;
			«ENDIF»
			
			«IF version.isAfter(JUNO_SR1_RANGE)»
				handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.windowBackgroundColor.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
			«ELSE»
				«IF config.useEmbossedDragHandle»
					handle-image: url(images/handle-embossed.png);
				«ELSE»
					handle-image: url(images/handle.png);
				«ENDIF»
			«ENDIF»
		}
		
		«IF version.isAfter(JUNO_SR1_RANGE)»
			.MTrimBar .Draggable {
				handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.windowBackgroundColor.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
			}
			
			.MTrimBar#org-eclipse-ui-main-toolbar .Draggable {
				handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.toolbarGradientStart.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
			}
			
			.MTrimBar#org-eclipse-ui-main-toolbar .TrimStack {
				«IF config.useTrimStackImageBorder»
					frame-image: url(chrome://frame?background-color=«config.toolbarGradientStart.toHSB.toHTMLCode»);
				«ENDIF»
				handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.toolbarGradientStart.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
			}
			
			«IF !config.useWindowBackgroundAsStatusBarBackground»
				.MTrimBar#org-eclipse-ui-trim-status .Draggable {
					handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.statusBarBackgroundColor.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
				}
				
				.MTrimBar#org-eclipse-ui-trim-status .TrimStack {
					«IF config.useTrimStackImageBorder»
						frame-image: url(chrome://frame?background-color=«config.statusBarBackgroundColor.toHSB.toHTMLCode»);
					«ENDIF»
					handle-image: url(chrome://drag-handle?height=«getToolbarHeight»&background-color=«config.statusBarBackgroundColor.toHSB.toHTMLCode»&embossed=«config.useEmbossedDragHandle»);
				}
			«ENDIF»
		«ENDIF»
		
		#org-eclipse-ui-main-toolbar #PerspectiveSwitcher {
			eclipse-perspective-keyline-color: «config.perspectiveOutlineColor.toHtmlColor»;
			background-color: «config.getPerspectiveStartColor.toHtmlColor» «config.perspectiveEndColor.toHtmlColor» 100%;
			«IF version.isAfter(JUNO_SR1_RANGE)»
				handle-image: none;
			«ENDIF»
			chrome-show-perspective-name: «config.showTextOnPerspectiveSwitcher»;
		}
		
		«IF version.isIncluded(JUNO_SR1_RANGE)»
			#PerspectiveSpacer{
				chrome-border-bottom-color: «config.perspectiveOutlineColor.toHtmlColor»;
				chrome-border-bottom-visible: true;
			}
		«ENDIF»
		
		.MPart.Editor StyledText{
			chrome-line-style: «config.editorLineStyle»;
			chrome-line-color: «config.editorLineColor.toHtmlColor»;
		}
		
		/* User CSS */
		«config.userCSS»
	'''

	def private HSB toHSB(RGB rgb) {
		return new HSB(rgb)
	}

	def private String toHtmlColor(RGB rgb) {
		return String::format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue)
	}
}
