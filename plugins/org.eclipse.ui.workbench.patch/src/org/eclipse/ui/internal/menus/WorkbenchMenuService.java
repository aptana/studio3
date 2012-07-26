/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.ContributionsAnalyzer;
import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuFactoryImpl;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;
import org.eclipse.e4.ui.workbench.renderers.swt.ContributionRecord;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.e4.ui.workbench.renderers.swt.ToolBarContributionRecord;
import org.eclipse.e4.ui.workbench.swt.factories.IRendererFactory;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.e4.compatibility.E4Util;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.internal.services.ServiceLocator;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IMenuService;

/**
 * @since 3.5
 * 
 */
public class WorkbenchMenuService implements IMenuService {

	private IEclipseContext e4Context;
	private ServiceLocator serviceLocator;
	private ExpressionContext legacyContext;
	private MenuPersistence persistence;
	private Map<AbstractContributionFactory, Object> factoriesToContributions = new HashMap<AbstractContributionFactory, Object>();
	private IWorkbenchWindow window;
	private Map<ContributionManager, MenuLocationURI> managers = new HashMap<ContributionManager, MenuLocationURI>();

	/**
	 * @param serviceLocator
	 * @param e4Context
	 */
	public WorkbenchMenuService(ServiceLocator serviceLocator, IEclipseContext e4Context) {
		this.serviceLocator = serviceLocator;
		this.e4Context = e4Context;

		persistence = new MenuPersistence(e4Context.get(MApplication.class), e4Context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.services.IServiceWithSources#addSourceProvider(org.eclipse
	 * .ui.ISourceProvider)
	 */
	public void addSourceProvider(ISourceProvider provider) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.services.IServiceWithSources#removeSourceProvider(org.
	 * eclipse.ui.ISourceProvider)
	 */
	public void removeSourceProvider(ISourceProvider provider) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.services.IDisposable#dispose()
	 */
	public void dispose() {
		persistence.dispose();
	}

	private boolean inToolbar(MenuLocationURI location) {
		return location.getScheme().startsWith("toolbar"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#addContributionFactory(org.eclipse.
	 * ui.menus.AbstractContributionFactory)
	 */
	public void addContributionFactory(final AbstractContributionFactory factory) {
		MenuLocationURI location = new MenuLocationURI(factory.getLocation());

		if (inToolbar(location)) {
			String path = location.getPath();
			if (path.equals(MenuAdditionCacheEntry.MAIN_TOOLBAR)
					|| path.equals(MenuAdditionCacheEntry.TRIM_COMMAND1)
					|| path.equals(MenuAdditionCacheEntry.TRIM_COMMAND2)
					|| path.equals(MenuAdditionCacheEntry.TRIM_VERTICAL1)
					|| path.equals(MenuAdditionCacheEntry.TRIM_VERTICAL2)
					|| path.equals(MenuAdditionCacheEntry.TRIM_STATUS)) {
				// processTrimChildren(trimContributions, toolBarContributions,
				// configElement);
			} else {
				String query = location.getQuery();
				if (query == null || query.length() == 0) {
					query = "after=additions"; //$NON-NLS-1$
				}
				processToolbarChildren(factory, location, path, query);
			}
			return;
		}
		MMenuContribution menuContribution = MenuFactoryImpl.eINSTANCE.createMenuContribution();
		menuContribution.setElementId(factory.getNamespace() + ":" + factory.hashCode()); //$NON-NLS-1$

		if ("org.eclipse.ui.popup.any".equals(location.getPath())) { //$NON-NLS-1$
			menuContribution.setParentId("popup"); //$NON-NLS-1$
		} else {
			menuContribution.setParentId(location.getPath());
		}
		String query = location.getQuery();
		if (query == null || query.length() == 0) {
			query = "after=additions"; //$NON-NLS-1$
		}
		menuContribution.setPositionInParent(query);
		menuContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$
		String filter = ContributionsAnalyzer.MC_MENU;
		if ("popup".equals(location.getScheme())) { //$NON-NLS-1$
			filter = ContributionsAnalyzer.MC_POPUP;
		}
		menuContribution.getTags().add(filter);
		ContextFunction generator = new ContributionFactoryGenerator(factory, 0);
		menuContribution.getTransientData().put(ContributionRecord.FACTORY, generator);
		factoriesToContributions.put(factory, menuContribution);
		MApplication app = e4Context.get(MApplication.class);
		app.getMenuContributions().add(menuContribution);

		// OK, now update any managers that use this uri
		for (Map.Entry<ContributionManager, MenuLocationURI> entry : managers.entrySet()) {
			MenuLocationURI mgrURI = entry.getValue();
			if (mgrURI.getScheme().equals(location.getScheme())
					&& mgrURI.getPath().equals(location.getPath())) {
				ContributionManager mgr = entry.getKey();
				populateContributionManager(mgr, mgrURI.toString());
				mgr.update(true);
			}
		}
	}

	private void processToolbarChildren(AbstractContributionFactory factory,
			MenuLocationURI location, String parentId, String position) {
		MToolBarContribution toolBarContribution = MenuFactoryImpl.eINSTANCE
				.createToolBarContribution();
		toolBarContribution.setElementId(factory.getNamespace() + ":" + factory.hashCode()); //$NON-NLS-1$
		toolBarContribution.setParentId(parentId);
		toolBarContribution.setPositionInParent(position);
		toolBarContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$

		ContextFunction generator = new ContributionFactoryGenerator(factory, 1);
		toolBarContribution.getTransientData().put(ToolBarContributionRecord.FACTORY, generator);
		factoriesToContributions.put(factory, toolBarContribution);
		MApplication app = e4Context.get(MApplication.class);
		app.getToolBarContributions().add(toolBarContribution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#removeContributionFactory(org.eclipse
	 * .ui.menus.AbstractContributionFactory)
	 */
	public void removeContributionFactory(AbstractContributionFactory factory) {
		Object contribution;
		if ((contribution = factoriesToContributions.remove(factory)) != null) {
			MApplication app = e4Context.get(MApplication.class);
			if (app == null)
				return;
			if (contribution instanceof MMenuContribution) {
				app.getMenuContributions().remove(contribution);
			} else if (contribution instanceof MToolBarContribution) {
				app.getToolBarContributions().remove(contribution);
			}
		}

		// OK, now remove any managers that use this uri
		MenuLocationURI location = new MenuLocationURI(factory.getLocation());
		List<ContributionManager> toRemove = new ArrayList<ContributionManager>();
		for (Map.Entry<ContributionManager, MenuLocationURI> entry : managers.entrySet()) {
			MenuLocationURI mgrURI = entry.getValue();
			if (mgrURI.getScheme().equals(location.getScheme())
					&& mgrURI.getPath().equals(location.getPath())) {
				toRemove.add(entry.getKey());
			}
		}
		for (ContributionManager mgr : toRemove) {
			mgr.removeAll();
			managers.remove(mgr);
		}
	}

	protected IWorkbenchWindow getWindow() {
		if (serviceLocator == null)
			return null;

		IWorkbenchLocationService wls = (IWorkbenchLocationService) serviceLocator
				.getService(IWorkbenchLocationService.class);

		if (window == null) {
			window = wls.getWorkbenchWindow();
		}
		if (window == null) {
			IWorkbench wb = wls.getWorkbench();
			if (wb != null) {
				window = wb.getActiveWorkbenchWindow();
			}
		}
		return window;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#populateContributionManager(org.eclipse
	 * .jface.action.ContributionManager, java.lang.String)
	 */
	public void populateContributionManager(ContributionManager mgr, String location) {
		MenuLocationURI uri = new MenuLocationURI(location);
		// Track this call by recording the manager and location!
		if (!managers.containsKey(mgr)) {
			managers.put(mgr, uri);
		}

		// Now handle registering dynamic additions by querying E4 model
		if (!(mgr instanceof MenuManager)) {
			return;
		}
		MenuManager menu = (MenuManager) mgr;
		MMenu menuModel = getMenuModel(menu, uri);

		IRendererFactory factory = e4Context.get(IRendererFactory.class);
		AbstractPartRenderer obj = factory.getRenderer(menuModel, null);
		if (obj instanceof MenuManagerRenderer) {
			MenuManagerRenderer renderer = (MenuManagerRenderer) obj;
			renderer.reconcileManagerToModel(menu, menuModel);
			renderer.processContributions(menuModel, false, false);
			// double cast because we're bad people
			renderer.processContents((MElementContainer<MUIElement>) ((Object) menuModel));
		}
	}

	protected MMenu getMenuModel(MenuManager menu, MenuLocationURI location) {
		// FIXME See if we can find the already existing matching menu with this
		// id?
		if ("org.eclipse.ui.main.menu".equals(location.getPath())) //$NON-NLS-1$
		{
			WorkbenchWindow workbenchWindow = (WorkbenchWindow) getWindow();
			MWindow window = workbenchWindow.getModel();
			return window.getMainMenu();
		}

		// TODO Otherwise create one
		MMenu menuModel = null;
		//		if ("popup".equals(location.getScheme())) { //$NON-NLS-1$
		// menuModel = MenuFactoryImpl.eINSTANCE.createPopupMenu();
		// menuModel.getTags().add(ContributionsAnalyzer.MC_POPUP);
		// } else {
		menuModel = MenuFactoryImpl.eINSTANCE.createMenu();
		menuModel.getTags().add(ContributionsAnalyzer.MC_MENU);
		// }
		menuModel.setLabel(menu.getMenuText());
		menuModel.setElementId(menu.getId());
		if (menuModel.getElementId() == null) {
			menuModel.setElementId(location.getPath());
		}

		MPart modelPart = (MPart) e4Context.get(IServiceConstants.ACTIVE_PART);
		if (modelPart != null) {
			modelPart.getMenus().add(menuModel);
		}
		IRendererFactory factory = e4Context.get(IRendererFactory.class);
		AbstractPartRenderer obj = factory.getRenderer(menuModel, null);
		if (obj instanceof MenuManagerRenderer) {
			MenuManagerRenderer renderer = (MenuManagerRenderer) obj;
			renderer.linkModelToManager(menuModel, menu);
		}

		return menuModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.menus.IMenuService#releaseContributions(org.eclipse.jface
	 * .action.ContributionManager)
	 */
	public void releaseContributions(ContributionManager mgr) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.menus.IMenuService#getCurrentState()
	 */
	public IEvaluationContext getCurrentState() {
		if (legacyContext == null) {
			legacyContext = new ExpressionContext(e4Context);
		}
		return legacyContext;
	}

	/**
	 * read in the menu contributions and turn them into model menu
	 * contributions
	 */
	public void readRegistry() {
		persistence.read();
	}

	public void updateManagers() {
		E4Util.unsupported("WorkbenchMenuService.updateManagers - time to update ... something"); //$NON-NLS-1$
	}

	/**
	 * @param item
	 * @param visibleWhen
	 * @param restriction
	 * @param createIdentifierId
	 */
	public void registerVisibleWhen(IContributionItem item, Expression visibleWhen,
			Set restriction, String createIdentifierId) {
		// TODO Remove - no longer used

	}

	/**
	 * @param item
	 * @param restriction
	 */
	public void unregisterVisibleWhen(IContributionItem item, Set restriction) {
		// TODO Remove - no longer used

	}

}
