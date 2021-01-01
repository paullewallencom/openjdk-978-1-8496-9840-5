/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cookbook.openjdk;

import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        SamplingProvider.initialize();
    }

    @Override
    public void uninstalled() {
        SamplingProvider.unregister();
    }
}
