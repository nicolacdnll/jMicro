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
package jmicro.v4l4jutils;

import au.edu.jcu.v4l4j.*;
import au.edu.jcu.v4l4j.exceptions.ControlException;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmicro.Globals;
import jmicro.Utils;

/**
 * A class that describes a Device device that needs to be connected at the computer.
 * Implements the CaptureCallBack interface the v4l4j and extends the Observable class.
 * This class also provide methods for access to all controls provided bye V4L2, the driver
 * and one method for release the device. * 
 * 
*
 */

public class Device extends Observable implements CaptureCallback {

    private final boolean _QX3_DEBUG = false;

    private VideoDevice videoDev = null;
    private Control topLight = null;
    private Control bottomLight = null;
    private Control brightness = null;
    private Control contrast = null;
    private Control saturation = null;
    private Control horizontalFlip = null;
    private Control verticalFlip = null;
    private Control whiteBalance = null;
    private Control whiteBalanceAuto = null;
    private Control gamma = null;
    private Control frequency = null;
    private Control hue = null;
    private boolean none;
    

    /**
     * Class constructor specifying the file of the device.
     * Initialize all the controls implemented by V4l2 and the drivers and turn on the top light
     * @param deviceFileName The absolute path of the Device's file
     */
    public Device(String deviceFileName) {
        if(deviceFileName.equals(Globals.NO_DEVICE_CODE)){
            none = true;      
        }else{
            none = false;
            try {

                videoDev = new VideoDevice(deviceFileName);
                ControlList controls = videoDev.getControlList();
                
                //Getting some device's controls
                topLight = controls.getControl("Illuminator 2"); //top light
                if(topLight != null) topLight.setValue(1);
                bottomLight = controls.getControl("Illuminator 1"); //bottom light

                contrast = controls.getControl("Contrast");
                brightness = controls.getControl("Brightness");
                saturation = controls.getControl("Saturation");
                hue = controls.getControl("Hue");

                frequency = controls.getControl("Power Line Frequency");
                if(frequency == null) frequency = controls.getControl("Light frequency filter");

                whiteBalanceAuto = controls.getControl("White Balance Temperature, Auto");
                if(whiteBalanceAuto == null) whiteBalanceAuto = controls.getControl("Whitebalance (software)");

                whiteBalance = controls.getControl("White Balance Temperature");

                gamma = controls.getControl("Gamma");
                if(gamma == null) gamma = controls.getControl("Gamma (software)");


                horizontalFlip = controls.getControl("Horizontal flip (sw)");
                verticalFlip = controls.getControl("Vertical flip (sw)");


                if (_QX3_DEBUG){
                    //Print all control name
                    System.out.print("All controls ");
                    for (Control l : controls.getList()) {
                        System.out.println(l.getName());
                    }
                }
            } catch (V4L4JException ex) {
                Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
 
   }


    public boolean isNone(){
        return none;
    }
    
    /**
     * Returns the absolute path file of the video device.
     * @return The absolute path file of the video device
     */
    public String getDeviceFile() {
        if(none)
            return Globals.NO_DEVICE_CODE;
        else
            return videoDev.getDevicefile();
    }

    public String getDeviceName(){
        if (null != videoDev) {
            try {
                return videoDev.getDeviceInfo().getName();
            } catch (V4L4JException ex) {
                Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
            
    /**
     * Turns off both the lights, releases the control list, stop the capture and finally releases the frame grabber and the device.
     *
     */
    public void releaseDevice() {
        try {
            if (this.topLight != null) topLight.setValue(0); //turn off top light
            if (this.bottomLight != null) bottomLight.setValue(0); //turn off bottom light
            
            if(!none){
                //release the controlList, the framegrabber and the device
                videoDev.releaseControlList();
                this.cleanupCapture();
                videoDev.releaseFrameGrabber();
                videoDev.release();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /*---------------------------------------------------------------------+
    |                           CONTROLS                                   |
    +---------------------------------------------------------------------*/

    /*--------------+
    |     Lights    |
    +--------------*/    
    /**
     * Checks if the device has a bottom light
     * @return true if the device has a bottom light (e.g QX devices), false otherwise
     */
    public boolean hasBottomLight(){
        if (this.bottomLight == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Checks if the device has a top light
     * @return true if the device has a top light (e.g QX devices), false otherwise
     */
    public boolean hasTopLight(){
        if (this.bottomLight == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Changes the state of the bottom light. 
     * For example if the light is on will be turned off and vice versa.
     */
    public void changeStateBottomLight() {
        try {
            if (this.bottomLight != null) {
                if (bottomLight.getValue() == 1) {//if the light is on
                    bottomLight.setValue(0); //Turn off the light
                } else { //otherwise (the light is off)
                    bottomLight.setValue(1); //Turn on the light
                }
            }
        } catch (ControlException e) {
            System.out.println("Got a ControlException " + e.getMessage());
        }
    }
    
    /**
     * Changes the state of the top light. 
     * For example if the light is on will be turned off and vice versa.
     */
    public void changeStateTopLight() {
        try {
            if (this.topLight != null) {
                if (topLight.getValue() == 1) {//if the light is on
                    topLight.setValue(0); //Turn off the light
                } else { //otherwise (the light is off)
                    topLight.setValue(1); //Turn on the light
                }
            }
        } catch (ControlException e) {
            System.out.println("Got a ControlException " + e.getMessage());
        }
    }

    
    /**
     * Sets the state of the bottom light.
     *@param state The state of the light, true turns on the light and false turns off.
     */
    public void setBottomLight(boolean state) {
        try {
            if (this.bottomLight != null) {
                if (state) { //if true
                    this.bottomLight.setValue(1); //turn on the light
                } else {
                    this.bottomLight.setValue(0); //turn off the light
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Tests if the bottom light is on.
     *@return true if the bottom light is on; false otherwise.
     */
    public boolean isBottomLightOn() {
        try {
            if (this.bottomLight != null) {
                if (bottomLight.getValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Sets the state of the top light.
     *@param state The state of the light, true turns on the light and false turns off.
     */
    public void setTopLight(boolean state) {
        try {
            if(this.topLight != null){
                if (state) { //if true
                    this.topLight.setValue(1); //turn on the light
                } else {
                    this.topLight.setValue(0); //turn off the light
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Tests if the top light is on.
     *@return true if the bottom light is on; false otherwise.
     */
    public boolean isTopLightOn() {
        try {
            if(this.topLight != null){
                if (topLight.getValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    
    /*------------------+
    |     Brightness    |
    +------------------*/
    /**
     * Returns an array with the values of the brightness control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getBrightnessInfo(){
        int info[] = new int[5];
        try {
            if(this.brightness != null){
                info[0] = this.brightness.getDefaultValue();
                info[1] = this.brightness.getMinValue();
                info[2] = this.brightness.getMaxValue();
                info[3] = this.brightness.getStepValue();
                info[4] = this.brightness.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    /**
    * Checks if the device has the brightness control
    * @return true if the device has the brightness control, false otherwise
    */
    public boolean hasBrightness(){
        if (this.brightness == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the brightness.
     * @return The value of the brightness
     */
    public int getBrightness() {
        int value = 0;
        try {
            if(this.brightness != null) value = brightness.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }    
    
    /**
     * Sets the value of the brightness.
     * @param value Value to be set
     */
    public void setBrightness(int value) {
        try {
            if (this.brightness == null) throw new Exception("isnull");
            if (value < this.brightness.getMinValue() || this.brightness.getMaxValue() < value) throw new Exception("outOfBound");
            brightness.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setBrightness() -> The device hasn't a Brightness control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setBrightness() -> " + value + "is out of bound for the Brightness control.");           
        }
    }
    
    /*----------------+
    |     Contrast    |
    +----------------*/
    /**
     * Returns an array with the values of the contrast control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getContrastInfo(){
        int info[] = new int[5];
        try {
            if(this.contrast != null){
                info[0] = this.contrast.getDefaultValue();
                info[1] = this.contrast.getMinValue();
                info[2] = this.contrast.getMaxValue();
                info[3] = this.contrast.getStepValue();
                info[4] = this.contrast.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    /**
    * Checks if the device has the contrast control
    * @return true if the device has the contrast control, false otherwise
    */
    public boolean hasContrast(){
        if (this.contrast == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the contrast.
     * @return The value of the Contrast; min 0, max 96
     */
    public int getContrast() {
        int value = 0;
        try {
            if (this.contrast != null) value = contrast.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * Sets the value of the Contrast.
     * @param value value to be set;  
     */
    public void setContrast(int value) {
        try {
            if (this.contrast == null) throw new Exception("isnull");
            if (value < this.contrast.getMinValue() || this.contrast.getMaxValue() < value) throw new Exception("outOfBound");
            contrast.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setContrast() -> The device hasn't a Contrast control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setContrast() -> " + value + "is out of bound for the Contrast control.");           
        }
    }
    
    
    /*----------------+
    |     Saturation  |
    +----------------*/
    /**
     * Returns an array with the values of the saturation control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getSaturationInfo(){
        int info[] = new int[5];
        try {
            if(this.saturation != null){
                info[0] = this.saturation.getDefaultValue();
                info[1] = this.saturation.getMinValue();
                info[2] = this.saturation.getMaxValue();
                info[3] = this.saturation.getStepValue();
                info[4] = this.saturation.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    /**
    * Checks if the device has the saturation control
    * @return true if the device has the saturation control, false otherwise
    */
    public boolean hasSaturation(){
        if (this.saturation == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the saturation.
     * @return The value of the Saturation
     */
    public int getSaturation() {
        int value = 0;
        try {
            if (this.saturation != null) value = saturation.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * Sets the value of the Saturation.
     * @param value value to be set;  
     */
    public void setSaturation(int value) {
        try {
            if (this.saturation == null) throw new Exception("isnull");
            if (value < this.saturation.getMinValue() || this.saturation.getMaxValue() < value) throw new Exception("outOfBound");
            saturation.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setSaturation() -> The device hasn't a Saturation control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setSaturation() -> " + value + "is out of bound for the Saturation control.");           
            //Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*---------+
    |     Hue  |
    +---------*/
    /**
     * Returns an array with the values of the hue control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getHueInfo(){
        int info[] = new int[5];
        try {
            if(this.hue != null){
                info[0] = this.hue.getDefaultValue();
                info[1] = this.hue.getMinValue();
                info[2] = this.hue.getMaxValue();
                info[3] = this.hue.getStepValue();
                info[4] = this.hue.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    /**
    * Checks if the device has the hue control
    * @return true if the device has the hue control, false otherwise
    */
    public boolean hasHue(){
        if (this.hue == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the hue.
     * @return The value of the Hue
     */
    public int getHue() {
        int value = 0;
        try {
            if (this.hue != null) value = hue.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * Sets the value of the Hue.
     * @param value value to be set;  
     */
    public void setHue(int value) {
        try {
            if (this.hue == null) throw new Exception("isnull");
            if (value < this.hue.getMinValue() || this.hue.getMaxValue() < value) throw new Exception("outOfBound");
            hue.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setHue() -> The device hasn't a Hue control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setHue() -> " + value + "is out of bound for the Hue control.");           
            //Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /*--------------------+
    |  White Balance Auto |
    +--------------------*/   
    /**
     * Checks if the device has the white balance auto control
     * @return true if the device has the white balance auto control, false otherwise
     */
    public boolean hasWhiteBalanceAuto(){
        if (this.whiteBalanceAuto == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Tests if the white balance is enabled.
     *@return True if the whiteBalance is enabled, false otherwise.
     */
    public boolean isWhiteBalanceAutoEnable() {
        try {
            if (this.whiteBalanceAuto != null){
                if (whiteBalanceAuto.getValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Sets the white balance.
     * @param enabled True enabled the withe balance, false disable.
     */
    public void setWhitebalance(boolean enabled) {
        try {
            if(whiteBalanceAuto != null){
                if (enabled == true) {
                    whiteBalanceAuto.setValue(1);
                } else {
                    whiteBalanceAuto.setValue(0);
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     
    /*-----------------+
    |   WhiteBalance   |
    +-----------------*/ 
    /**
     * Checks if the device has the whitebalance control
     * @return true if the device has the whitebalance control, false otherwise
     */
    public boolean hasWhiteBalance(){
        if (this.whiteBalance == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the whitebalance.
     * @return The value of the whitebalance
     */
    public int getWhiteBalance() {
        int value = -2147483648; //min integer value 
        try {       
            if (this.whiteBalance != null) value = whiteBalance.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * Sets the value of the whitebalance.
     * @param value Value to be set
     */
    public void setWhiteBalance(int value) {
        try {
            if (this.whiteBalance == null) throw new Exception("isnull");
            if (value < this.whiteBalance.getMinValue() || this.whiteBalance.getMaxValue() < value) throw new Exception("outOfBound");
            whiteBalance.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setwhiteBalance() -> The device hasn't a whiteBalance control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setwhiteBalance() -> " + value + "is out of bound for the whiteBalance control.");           
            //Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns an array with the values of the whitebalance control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getwhiteBalanceInfo(){
        int info[] = new int[5];
        try {
            if(this.whiteBalance != null){
                info[0] = this.whiteBalance.getDefaultValue();
                info[1] = this.whiteBalance.getMinValue();
                info[2] = this.whiteBalance.getMaxValue();
                info[3] = this.whiteBalance.getStepValue();
                info[4] = this.whiteBalance.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    /*----------+
    |   Gamma   |
    +----------*/ 
    /**
     * Checks if the device has the gamma control
     * @return true if the device has the gamma control, false otherwise
     */
    public boolean hasGamma(){
        if (this.gamma == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Returns the value of the gamma.
     * @return The value of the gamma.
     */
    public int getGamma() {
        int value = -2147483648; //min integer value 
        try {       
            if (this.gamma != null) value = gamma.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    /**
     * Sets the value of the gamma.
     * @param value Value to be set.
     */
    public void setGamma(int value) {
        try {
            if (this.gamma == null) throw new Exception("isnull");
            if (value < this.gamma.getMinValue() || this.gamma.getMaxValue() < value) throw new Exception("outOfBound");
            gamma.setValue(value);
        } catch (Exception ex) {
            if(ex.getMessage().equals("isnull")) System.err.println("Device.setGamma() -> The device hasn't a Gamma control.");
            if(ex.getMessage().equals("outOfBound")) System.err.println("Device.setGamma() -> " + value + "is out of bound for the Gamma control.");           
            //Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns an array with the values of the gamma control.
     * The array contains: default, minimum, maximum, step and actual values.
     * @return the array of the values
     */
    public int[] getGammaInfo(){
        int info[] = new int[5];
        try {
            if(this.gamma != null){
                info[0] = this.gamma.getDefaultValue();
                info[1] = this.gamma.getMinValue();
                info[2] = this.gamma.getMaxValue();
                info[3] = this.gamma.getStepValue();
                info[4] = this.gamma.getValue();
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    
    
    /*--------------------------+
    |  Horizontal/vertical flip |
    +---------------------------*/   
    /**
     * Checks if the device has the horizontal flip control
     * @return true if the device has the horizontal flip control, false otherwise
     */
    public boolean hasHorizontalFlip(){
        if (this.horizontalFlip == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Tests if the horizontal flip is enabled
     * @return True if the Horizontal flip is enabled; false otherwise.
     */
    public boolean isHorizontalFlipEnable() {
        try {
            if (this.horizontalFlip != null){ 
                if (horizontalFlip.getValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Sets the horizontal flip
     */
    public void setHorizontalFlip(boolean enabled) {
        try {
            if (this.horizontalFlip != null ){
                if (enabled == true) {
                    horizontalFlip.setValue(1);
                } else {
                    horizontalFlip.setValue(0);
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
    * Checks if the device has the vertical flip control
    * @return true if the device has the vertical flip control, false otherwise
    */
    public boolean hasVerticalFlip(){
        if (this.verticalFlip == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Tests if the vertical flip is enabled
     * @return True if the vertical Flip is enabled; false otherwise.
     */
    public boolean isVerticalFlipEnable() {
        try {
            if (this.verticalFlip != null) {
                if (verticalFlip.getValue() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Sets the horizontal flip
     */
    public void setVerticalFlip(boolean enabled) {
        try {
            if (this.verticalFlip != null) {
                if (enabled == true) {
                    verticalFlip.setValue(1);
                } else {
                    verticalFlip.setValue(0);
                }
            }
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    /*-----------------+
    |     Frequency    |
    +-----------------*/   
    /**
     * Checks if the device has a frequency control
     * @return true if the device has a frequency control, false otherwise
     */
    public boolean hasFrequency(){
        if (this.frequency == null) {
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Sets the value of the light Frequency.
     * @param value value to be set; 0 Disable, 1 50Hz and 2 60Hz.
     */
    public void setFrequency(int value) {
        try {
            if(frequency != null)
                frequency.setValue(value);
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the value of the light Frequency.
     * @return Value of the light Frequency, 0 Disable, 1 50Hz and 2 60Hz.
     */
    public int getFrequency() {
        int value = -2147483648; //min integer value 
        try {
            if(frequency != null)
                value = frequency.getValue();
        } catch (ControlException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    
    /*-----------------+
    |  NOT IMPLEMENTED |
    +-----------------*/   
    /**
     * Tests if the microscope is on the cradle.
     * NOTE: Method to implements
     * @return true if the microscope is on the cradle; false otherwise.
     */
    public boolean isCradled(){
        //test if the microscope is on cradle
        return true;
    }

    /**
     * Tests if the microscope's hardware button is pressed.
     * NOTE: Method to implements
     * @return true if the microscope is pressed; false otherwise.
     */
    public boolean isButtonPressed() {
        //test if the microscope's hardware button is pressed
        return true;
    }


    /**
     * Returns the last captured image.
     * @return The last captured image.
     * @see BufferedImage
     */
    public BufferedImage getImage() {
        return image;
    }
    
    
    /*-----------------------------------------------------------------------+
    |              Implementation of the Observable Patter                   |
    +-----------------------------------------------------------------------*/
    protected Observer observer;

    @Override
    public void notifyObservers() {
        if (observer != null) {
            observer.update(this, image);
        }
    }

    @Override
    public void addObserver(Observer _observer) {
        if (_FRAMEGRUBBER_DEBUG) {
            System.out.println("Device::addObserver()");
        }
        if(observer == null) {
            observer = _observer;
//            this.startCapture();
        } else 
            System.out.println(_observer.getClass().toString());
        
        
    }

    @Override
    public void deleteObserver(Observer _observer) {
//        this.cleanupCapture();
        observer = null;
        
    }

    /*-----------------------------------------------------------------------+
    |               Abstract method from CaptureCallback                     |
    +-----------------------------------------------------------------------*/
    private final boolean _FRAMEGRUBBER_DEBUG = false;
    //Define the frameGrabber and V4L4J vars
    private FrameGrabber frameGrabber;
    private BufferedImage image;/// = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    private int std     = V4L4JConstants.STANDARD_WEBCAM;
    private int quality = V4L4JConstants.MAX_JPEG_QUALITY-10;
    private int channel = 0;

    /**
     *
     * @param e
     */
    @SuppressWarnings("CallToThreadDumpStack")
    @Override
    public void exceptionReceived(V4L4JException e) {
        // This method is called by v4l4j if an exception
        // occurs while waiting for a new frame to be ready.
        // The exception is available through e.getCause()
        e.printStackTrace();
        cleanupCapture();
        startCapture();
    }

    /**
     * Initializes the FrameGrabber object
     */
    public void initFrameGrabber() {
        if (_FRAMEGRUBBER_DEBUG) {
            System.out.println("MainForm.initFrameGrabber()");
        }
        try {
//                   V4L4JConstants.MAX_WIDTH,
//                   V4L4JConstants.MAX_HEIGHT,
//            for (ImageFormat i : videoDev.getDeviceInfo().getFormatList().getNativeFormats()) {
//                Main.print(i.toString());
//            }
//            
//            if(!videoDev.supportJPEGConversion()){ // use the other supportXXXConversion() for other formats
//                Main.print("Does NOT support JPEG conversion");
//            }else{
//                Main.print("Supports JPEG conversion, quality = "+quality);
//            }
            
            frameGrabber = videoDev.getJPEGFrameGrabber(
                    640,
                    480,
                    channel,
                    std,
                    quality);

            if (frameGrabber == null) {
                //if frameGrabber is null the exception shuld be catch
                throw new UnsupportedOperationException("Framegrabber still null");
            } else {

                frameGrabber.setCaptureCallback(this);

                if (_FRAMEGRUBBER_DEBUG) {
                    System.out.println("FrameGrubber set");
                }
            }
        } catch (V4L4JException e) {
            System.err.println("Error in MainFrame.initFrameGrabber()");
            System.err.println("Message:\n" + e.getMessage());
            System.err.println("Cause:\n" + e.getCause());
        }
    }

    /**
     * Starts the capture from the frame grabber.
     * Initializes the frame grabber if is null.
     */
    private boolean isCapturing = false;
    public void startCapture() {
        if ( isCapturing ) {
            Utils.print("The device is already capturing");
        } else {
            if (videoDev == null){
                System.err.println("Can't start capture with none device");
            } else {
                // Start capture
                try {
                    if (frameGrabber == null) {
                        initFrameGrabber();
                    }

                    frameGrabber.startCapture();
                    isCapturing = true;

                    if (_FRAMEGRUBBER_DEBUG) {
                        System.out.println("FrameGrabber Started to Capture");
                    }

                } catch (V4L4JException e) {
                    System.err.println("Error in MainFrame.startCapture()");
                    System.err.println("Message:\n" + e.getMessage());
                    System.err.println("Cause:\n" + e.getCause());
                    System.exit(1);
                }
            }
        }
    }

    /**
     * Stops the capture and releases the frame grabber and video device.
     */
    public void cleanupCapture() {
        if (_FRAMEGRUBBER_DEBUG) {
                System.out.println("MainForm.cleanupCapture()");
        }
        
        if ( !isCapturing ) {
            if (_FRAMEGRUBBER_DEBUG) Utils.print("The device is not capturing, impossible to stop.");
        } else {
            try {
                if (frameGrabber != null) {
                    frameGrabber.stopCapture();  
                    isCapturing = false;
                }
            } catch (StateException e) {
                /*
                 * System.err.println("Error in MainFrame.cleanupCapture()");
                 * System.err.println("Message:\n" + e.getMessage());
                 * System.err.println("Cause:\n" + e.getCause());
                 */
            }
        }
    }

    /**
     * Manages the new frame and notify all the observers.
     * @param frame
     */
    @Override
    public void nextFrame(VideoFrame frame) {
        image = frame.getBufferedImage();
        frame.recycle();
        this.notifyObservers();
    }
}