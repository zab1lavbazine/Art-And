package art.example.modules

import art.example.navigation.ExtendedMenuItem
import art.example.navigation.GeneralMenuItem
import art.example.navigation.MenuItem

class MenuItemBuilder {

    private val items = mutableListOf<MenuItem>()

    // Add a GeneralMenuItem
    fun addItem(label: String, onClick: () -> Unit): MenuItemBuilder {
        items.add(GeneralMenuItem(label, onClick))
        return this
    }

    // Add an ExtendedMenuItem
    fun <T> addExtendedItem(label: String, elementId: T, onClick: (T) -> Unit): MenuItemBuilder {
        items.add(ExtendedMenuItem(label, onClick, elementId))
        return this
    }

    // Build the list of menu items
    fun build(): List<MenuItem> {
        return items
    }
}
