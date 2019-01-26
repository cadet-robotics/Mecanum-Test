package frc.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Contains the code that loads controls and ports from the config file
 */
public class ControlsLoader {
    
    public Map<String, Integer> load(String section) throws IOException {
        HashMap<String, Integer> retMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(Filesystem.getDeployDirectory() + "/controls_config.cfg"));
        
        String line = "";
        boolean inSection = false;
        
        while((line = br.readLine()) != null){
            //Find segment
            if(line.equals(section)){
                inSection = true;
                continue;
            } else if(!inSection) {
                continue;
            }
            
            //End of segment
            if(line.equals("")){
                break;
            }
            
            //Parse and add to map
            String[] sa = line.split(" ");
            
            retMap.put(sa[0], Integer.parseInt(sa[1]));
        }
        
        br.close();
        
        return retMap;
    }
}