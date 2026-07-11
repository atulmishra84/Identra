package com.identra.adapter.spi;

import com.identra.canonical.Identity;

import java.util.List;

public interface SyncAdapter {

    List<Identity> fullExport();

    List<Identity> deltaExport(String watermark);
}
