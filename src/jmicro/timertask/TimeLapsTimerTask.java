/*
 * Copyright (C) 2015 Nicola Cadenelli (nicolacdnll@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jmicro.timertask;

import java.io.File;
import java.util.TimerTask;
import jmicro.SettingsManager;
import jmicro.Utils;
import jmicro.gui.mvc.main.MModel;
import jmicro.gui.mvc.main.MView;

/**
 * //TODO: Could be nice to do not interact with model and view but with the control
 * , control which with interact with them for us.
 * TimerTasks that wakes up every second and updates the countdown on the view,
 * freezes the GUI in case the countdown reaches the focus time in the model, and
 * takes a pictures when the countdowns is at zero. 
 * 
 * Nicola Cadenelli
 */
public class TimeLapsTimerTask extends TimerTask{

    private int             timeLeft;
    private int             count;
    private String          intervalText;
    private final int       intervalSeconds;

    private final MView      view;  
    private final MModel     model;
    private final String    dir;

    private boolean         topLampBeforeFrozen;
    private boolean         bottomLampBeforeFrozen;
    private final boolean   topLamp, bottomLamp;

    /**
     * 
     * @param interval The interval in seconds
     * @param dir   The absolute path of the directory where the picture will be stored. 
     * @param view  The view that has to be updated.
     * @param model The model that has to be updated.
     */
    public TimeLapsTimerTask(int interval, String dir, MView view, MModel model){
        intervalSeconds = timeLeft = interval;
        this.view   =   view;
        this.model  =   model;    
        this.dir    =   dir;
        count       =   0;
        topLamp     =   model.getDevice().isTopLightOn();
        bottomLamp  =   model.getDevice().isBottomLightOn();
    }
    
    @Override
    public void run() {
        
        intervalText = String.valueOf(timeLeft/60) 
               + ":" + String.valueOf(timeLeft%60);
        view.setCountdownLabel(intervalText);
        
        if (timeLeft == 0) {
                // Save a pic on the directory
                count++;
                // TODO CHANGE THIS ONCE A SETTING CLASS IS MADE
                String f = "image-" + String.format("%04d", count) + "."+SettingsManager.getImagesExtension();
                Utils.savePictureWithAbsoluteFileName(model.getDevice().getImage(), dir + File.separator + f);
                
                // Sets the lamps
                model.getDevice().setBottomLight(bottomLampBeforeFrozen);
                model.getDevice().setTopLight(topLampBeforeFrozen);
                // Updates model and view 
                view.updateTimeLapsCount(String.valueOf(count));
                  
                // Restarts the countdown
                timeLeft = intervalSeconds+1;
                
                // Defrezes only if the interval is greater than the focus time
                if ( timeLeft  > model.getFocusTime() ) {
                    model.setFrozen(false);
                    view.adaptToFrozen(model.isFrozen(), model.getMode());
                }
                
        } else if ( timeLeft <= model.getFocusTime() && !model.isFrozen()) {
                // Reads the status of the lamps
                topLampBeforeFrozen     =   model.getDevice().isTopLightOn();
                bottomLampBeforeFrozen  =   model.getDevice().isBottomLightOn();
                // Sets the lamps
                model.getDevice().setBottomLight(bottomLamp);
                model.getDevice().setTopLight(topLamp);
                // Updates model and view
                model.setFrozen(true);
                view.adaptToFrozen(model.isFrozen(), model.getMode());
        }
        timeLeft = timeLeft - 1;
    }
}
