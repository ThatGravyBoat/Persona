package tech.thatgravyboat.persona.common.management;

import tech.thatgravyboat.persona.api.NpcData;

public class NpcDataHolder {

    private NpcData data;
    private boolean dirty;

    private NpcDataHolder(NpcData data) {
        this.data = data;
    }

    public static NpcDataHolder of(NpcData data) {
        return new NpcDataHolder(data);
    }

    public static NpcDataHolder dirty(NpcData data) {
        NpcDataHolder holder = new NpcDataHolder(data);
        holder.setDirty(true);
        return holder;
    }

    public void setData(NpcData data) {
        this.data = data;
    }

    public NpcData getData() {
        return this.data;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}
