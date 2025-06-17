package org.wip.womtoolkit.components.collapsablesidebarmenu

import javafx.beans.property.BooleanProperty

//For now it just prevent people from putting garbage inside CollapsableSidebarMenu
interface CollapsableItem {
	var selectable: Boolean
	var localizaionKey: String?
	val onActionProperty : BooleanProperty
	fun select()
	fun deselect()
	fun expand()
	fun collapse()
}