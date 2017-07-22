/*
 *         Copyright 2017 Ton Ly (BreadMoirai)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package net.breadmoirai.samurai.modules.item;

import samurai.messages.base.SamuraiMessage;

public class BaseItem implements Item {

    private final ItemData data;

    BaseItem(ItemData data) {
        this.data = data;
    }

    @Override
    public ItemData getData() {
        return data;
    }

    @Override
    public SamuraiMessage useItem(ItemUseContext context) {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseItem{");
        sb.append(data);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Item && getData().getItemId() == ((Item) o).getData().getItemId();
    }

    @Override
    public int hashCode() {
        return getData().hashCode();
    }
}
