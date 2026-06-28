package dev.lvstrng.venum.event.events;

import dev.lvstrng.venum.event.CancellableEvent;
import dev.lvstrng.venum.event.Listener;

import java.util.ArrayList;

public interface ItemUseListener extends Listener {
	void onItemUse(ItemUseEvent event);

	class ItemUseEvent extends CancellableEvent<ItemUseListener> {
		@Override
		public void fire(ArrayList<ItemUseListener> listeners) {
			listeners.forEach(e -> e.onItemUse(this));
		}

		@Override
		public Class<ItemUseListener> getListenerType() {
			return ItemUseListener.class;
		}
	}
}
