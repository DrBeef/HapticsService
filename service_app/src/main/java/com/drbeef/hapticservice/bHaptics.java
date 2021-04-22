/*
   Copyright 2021 Simon Brown (DrBeef)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.drbeef.hapticservice;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.content.Context;
import android.util.Log;

import com.bhaptics.bhapticsmanger.BhapticsManager;
import com.bhaptics.bhapticsmanger.BhapticsManagerCallback;
import com.bhaptics.bhapticsmanger.BhapticsModule;
import com.bhaptics.bhapticsmanger.DefaultHapticStreamer;
import com.bhaptics.bhapticsmanger.HapticStreamer;
import com.bhaptics.bhapticsmanger.HapticPlayer;
import com.bhaptics.commons.PermissionUtils;
import com.bhaptics.commons.model.BhapticsDevice;
import com.bhaptics.commons.model.DotPoint;
import com.bhaptics.commons.model.PositionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;



public class bHaptics {
    
    public static class Haptic
    {
        Haptic(PositionType positionType, String key, String altKey, String group, float intensity, float duration) {
            this.positionType = positionType;
            this.key = key;
            this.altKey = altKey;
            this.group = group;
            this.directional = false;
            this.intensity = intensity;
            this.duration = duration;
            this.rotation = 0;
            this.level = 100;
            this.started = 0;
        }

        public Haptic(Haptic haptic) {
            this.positionType = haptic.positionType;
            this.key = haptic.key;
            this.altKey = haptic.altKey;
            this.group = haptic.group;
            this.directional = haptic.directional;
            this.intensity = haptic.intensity;
            this.duration = haptic.duration;
            this.rotation = 0;
            this.level = 100;
            this.started = 0;
        }

        public final String key;
        public final String altKey;
        public final String group;

        public boolean directional; // can be changed for specific repeating patterns
        public final float duration;
        public final PositionType positionType;
        public final float intensity;
        public long started; // used for repeating haptics - time (in ms) when started

        //These two values can be changed over time when playing a looping effect
        public float rotation;
        public float level;
    };
    
    public static final String TAG = "HapticService_bHaptics";

    public static final int BHAPTICS_PERMISSION_REQUEST = 1;

    private static Random rand = new Random();

    private static boolean hasPairedDevice = false;

    private static boolean enabled = false;
    private static boolean requestingPermission = false;
    private static boolean initialised = false;

    private static HapticPlayer player;
    private static HapticStreamer hapticStreamer;

    private static Context context;

    private static Map<String, Map<String, Vector<Haptic>>> applicationEventToPatternKeyMap = new HashMap<>();

    private static Map<String, Map<String, Haptic>> repeatingHaptics = new HashMap<>();

    private static Set<String> ignoreList = new HashSet<String>();

    public static void initialise()
    {
        if (initialised)
        {
            Log.d(TAG, "initialise called but already initialised");
            //Already initialised, but might need to rescan
            scanIfNeeded();
            return;
        }

        Log.d(TAG, "BhapticsModule.initialize");
        BhapticsModule.initialize(context);

        scanIfNeeded();

        player = BhapticsModule.getHapticPlayer();
        
        /////////////////////////////////////////////////////////////////////////////////////
        //                             Doom3Quest  Patterns
        /////////////////////////////////////////////////////////////////////////////////////
        Log.d(TAG, "BhapticsModule.initialize:  Registering Patterns");

        /*
            DAMAGE
        */
        registerFromAsset(context, "Doom3Quest", "Damage/Body_Heartbeat.tact", PositionType.Vest, "heartbeat", "health", 1.0f, 1.0f);

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Melee1.tact", "melee_left", "damage1");
        registerFromAsset(context, "Doom3Quest", "Damage/Head/DMG_Melee1.tact", PositionType.Head, "melee_left", "damage"); // always play melee on the head too

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Melee2.tact", "melee_right", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Head/DMG_Melee2.tact", PositionType.Head, "melee_right", "damage"); // always play melee on the head too

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Fireball.tact", "fireball", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Head/DMG_Explosion.tact", PositionType.Head, "fireball", "damage"); // always play fireball on the head too

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Bullet.tact", "bullet", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Head/DMG_HeadShot.tact", PositionType.Head, "bullet", "damage");

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Shotgun.tact", "shotgun", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Head/DMG_Explosion.tact", PositionType.Head, "shotgun", "damage");

        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Fire.tact", "fire", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Fire.tact", "noair", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Body_DMG_Falling.tact", "fall", "damage");
        registerFromAsset(context, "Doom3Quest", "Damage/Body_Shield_Break.tact", "shield_break", "damage");

         /*
            INTERACTIONS
         */
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Shield_Get.tact", "pickup_shield", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Pickup_L.tact", PositionType.ForearmL, "pickup_shield", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Pickup_R.tact", PositionType.ForearmR, "pickup_shield", "pickup");

        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Weapon_Get.tact", "pickup_weapon", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/ItemPickup_Mirror.tact", PositionType.ForearmL, "pickup_weapon", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/ItemPickup.tact", PositionType.ForearmR, "pickup_weapon", "pickup");

        //registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Ammo_Get.tact", "pickup_ammo", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Ammo_L.tact", PositionType.ForearmL, "pickup_ammo", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Ammo_R.tact", PositionType.ForearmR, "pickup_ammo", "pickup");

        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Healstation.tact", "healstation", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Healthstation_L.tact", PositionType.ForearmL, "healstation", "pickup");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/Healthstation_R.tact", PositionType.ForearmR, "healstation", "pickup");

        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/DoorSlide.tact", PositionType.Vest, "doorslide", "door", 1.0f, 0.5f);
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Scan.tact", PositionType.Vest, "scan", "environment", 1.0f, 1.15f);
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Scan.tact", PositionType.Vest, "decontaminate", "environment", 0.5f, 0.75f);
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Chamber_Up.tact", "liftup", "environment");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Chamber_Down.tact", "liftdown", "environment");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_Machine.tact", "machine", "environment");

        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Spark.tact", "spark", "local_effects");
        registerFromAsset(context, "Doom3Quest", "Interaction/Head/Spark.tact", PositionType.Head, "spark", "local_effects", 0.5f, 0.5f);

        //Directional based looping steam pattern
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Steam.tact", PositionType.Vest, "steam_loop", "local_effects", 0.5f, 1.0f);
        registerFromAsset(context, "Doom3Quest", "Interaction/Head/Steam.tact", PositionType.Head, "steam_loop", "local_effects", 0.5f, 1.0f);
        applicationEventToPatternKeyMap.get("Doom3Quest").get("steam_loop").forEach((haptic) -> {
            haptic.directional = true;
        });

        //Directional based looping flames pattern (use steam, but stronger)
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Steam.tact", PositionType.Vest, "flame_loop", "local_effects", 1.0f, 1.0f);
        registerFromAsset(context, "Doom3Quest", "Interaction/Head/Steam.tact", PositionType.Head, "flame_loop", "local_effects", 1.0f, 1.0f);
        applicationEventToPatternKeyMap.get("Doom3Quest").get("flame_loop").forEach((haptic) -> {
            haptic.directional = true;
        });

        //Re use the spark for the steam blast
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Spark.tact", PositionType.Vest, "steam_blast", "local_effects", 1.0f, 0.25f);

        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_PDA_Open.tact", "pda_open", "pda");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_PDA_Open.tact", "pda_close", "pda");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_PDA_Alarm.tact", "pda_alarm", "pda");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/Body_PDA_Touch.tact", "pda_touch", "pda");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/PDA_Click_Mirror.tact", PositionType.ForearmL, "pda_touch", "pda");
        registerFromAsset(context, "Doom3Quest", "Interaction/Arms/PDA_Click.tact", PositionType.ForearmR, "pda_touch", "pda");


        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/PlayerJump.tact", "jump_start", "player");
        registerFromAsset(context, "Doom3Quest", "Interaction/Vest/PlayerLanding.tact", "jump_landing", "player");


        /*
            WEAPONS
         */

        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/WeaponSwap.tact", PositionType.Right, "weapon_switch", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Swap_R.tact", PositionType.ForearmR, "weapon_switch", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/WeaponSwap_Mirror.tact", PositionType.Left, "weapon_switch", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Swap_L.tact", PositionType.ForearmL, "weapon_switch", "weapon");

        //Reload Start
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Body_Reload.tact", "weapon_reload", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Reload_L.tact", PositionType.ForearmL, "weapon_reload", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Reload_R.tact", PositionType.ForearmR, "weapon_reload", "weapon");

        //Reload Finish
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/ReloadFinish.tact", PositionType.Right, "weapon_reload_finish", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/ReloadFinish.tact", PositionType.ForearmR, "weapon_reload_finish", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/ReloadFinish_Mirror.tact", PositionType.Left, "weapon_reload_finish", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/ReloadFinish_Mirror.tact", PositionType.ForearmL, "weapon_reload_finish", "weapon");

        //Chainsaw Idle
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Chainsaw_LV1.tact", PositionType.Right, "chainsaw_idle", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Chainsaw_LV2.tact", PositionType.ForearmR, "chainsaw_idle", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Chainsaw_LV1_Mirror.tact", PositionType.Left, "chainsaw_idle", "weapon");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Chainsaw_LV2_Mirror.tact", PositionType.ForearmL, "chainsaw_idle", "weapon");

        //Chainsaw Fire
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Chainsaw_LV2.tact", PositionType.Right, "chainsaw_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Chainsaw_LV1.tact", PositionType.ForearmR, "chainsaw_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Chainsaw_LV2_Mirror.tact", PositionType.Left, "chainsaw_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Chainsaw_LV1_Mirror.tact", PositionType.ForearmL, "chainsaw_fire", "weapon_fire");

        //Fist
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Fist_Mirror.tact", PositionType.Left, "punch", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Fist_Mirror.tact", PositionType.ForearmL, "punch", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Fist.tact", PositionType.Right, "punch", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Fist.tact", PositionType.ForearmR, "punch", "weapon_fire");

        //Pistol
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV3_Mirror.tact", PositionType.Left, "pistol_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV3_Mirror.tact", PositionType.ForearmL, "pistol_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV3.tact", PositionType.Right, "pistol_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV3.tact", PositionType.ForearmR, "pistol_fire", "weapon_fire");

        //Shotgun
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV2_Mirror.tact", PositionType.Left, "shotgun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV2_Mirror.tact", PositionType.ForearmL, "shotgun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV2.tact", PositionType.Right, "shotgun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV2.tact", PositionType.ForearmR, "shotgun_fire", "weapon_fire");

        //Plasma Gun
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV1_Mirror.tact", PositionType.Left, "plasmagun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV1_Mirror.tact", PositionType.ForearmL, "plasmagun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV1.tact", PositionType.Right, "plasmagun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV1.tact", PositionType.ForearmR, "plasmagun_fire", "weapon_fire");

        //Grenade
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Body_Grenade_Init.tact", "handgrenade_init", "weapon_init");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Body_Grenade_Throw.tact", "handgrenade_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Grenade_L.tact", PositionType.ForearmL, "handgrenade_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Grenade_R.tact", PositionType.ForearmR, "handgrenade_fire", "weapon_fire");

        //SMG
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV1_Mirror.tact", PositionType.Left, "machinegun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV1_Mirror.tact", PositionType.ForearmL, "machinegun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV1.tact", PositionType.Right, "machinegun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV1.tact", PositionType.ForearmR, "machinegun_fire", "weapon_fire");

        //Chaingun
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Body_Chaingun_Init.tact", "chaingun_init", "weapon_init");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV2_Mirror.tact", PositionType.Left, "chaingun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV2_Mirror.tact", PositionType.ForearmL, "chaingun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV2.tact", PositionType.Right, "chaingun_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV2.tact", PositionType.ForearmR, "chaingun_fire", "weapon_fire");

        //BFG9000
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Body_BFG9000_Init.tact", "bfg_init", "weapon_init");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV5_Mirror.tact", PositionType.Left, "bfg_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV5_Mirror.tact", PositionType.ForearmL, "bfg_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV5.tact", PositionType.Right, "bfg_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV5.tact", PositionType.ForearmR, "bfg_fire", "weapon_fire");

        //Rocket Launcher
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV4_Mirror.tact", PositionType.Left, "rocket_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV4_Mirror.tact", PositionType.ForearmL, "rocket_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/Recoil_LV4.tact", PositionType.Right, "rocket_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/Recoil_LV4.tact", PositionType.ForearmR, "rocket_fire", "weapon_fire");

        //Soul Cube
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/SoulCube.tact", PositionType.Right, "soul_cube_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/SoulCube.tact", PositionType.ForearmR, "soul_cube_fire", "weapon_fire", 2.0f, 1.0f);
        registerFromAsset(context, "Doom3Quest", "Weapon/Vest/SoulCube_Mirror.tact", PositionType.Left, "soul_cube_fire", "weapon_fire");
        registerFromAsset(context, "Doom3Quest", "Weapon/Arms/SoulCube_Mirror.tact", PositionType.ForearmL, "soul_cube_fire", "weapon_fire", 2.0f, 1.0f);

        Log.d(TAG, "BhapticsModule.initialize:  " + applicationEventToPatternKeyMap.get("Doom3Quest").size() + " Patterns Resgitered for app: Doom3Quest");

        initialised = true;
    }

    public static void registerFromAsset(Context context, String application, String filename, PositionType type, String key, String group, float intensity, float duration)
    {
        String content = read(context, "bHaptics/" + application + "/" + filename);
        if (content != null) {

            String hapticKey = key + "_" + type.name();
            player.registerProject(hapticKey, content);

            UUID uuid = UUID.randomUUID();
            Haptic haptic = new Haptic(type, hapticKey, uuid.toString(), group, intensity, duration);

            Map<String, Vector<Haptic>> eventMap;
            if (!applicationEventToPatternKeyMap.containsKey(application))
            {
                eventMap = new HashMap<String, Vector<Haptic>>();
                applicationEventToPatternKeyMap.put(application, eventMap);
            }
            else
            {
                eventMap = applicationEventToPatternKeyMap.get(application);
            }

            if (!eventMap.containsKey(key))
            {
                Vector<Haptic> haptics = new Vector<>();
                haptics.add(haptic);
                eventMap.put(key, haptics);
            }
            else
            {
                Vector<Haptic> haptics = eventMap.get(key);
                haptics.add(haptic);
            }
        }
    }

    public static void registerFromAsset(Context context, String application, String filename, String key, String group)
    {
        registerFromAsset(context, application, filename, PositionType.Vest, key, group, 1.0f, 1.0f);
    }

    public static void registerFromAsset(Context context, String application, String filename, PositionType type, String key, String group)
    {
        registerFromAsset(context, application, filename, type, key, group, 1.0f, 1.0f);
    }

    public static void destroy()
    {
        if (initialised) {
            initialised = false;
            BhapticsModule.destroy();
        }
        enabled = false;
    }

    public static boolean hasPermissions() {
        boolean blePermission = PermissionUtils.hasBluetoothPermission(context);
        boolean filePermission = PermissionUtils.hasFilePermissions(context);
        return blePermission && filePermission;
    }

    public static void requestPermissions(Context ctx) {
        if (!requestingPermission) {
            requestingPermission = true;
            ActivityCompat.requestPermissions((Activity) ctx,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    BHAPTICS_PERMISSION_REQUEST);
        }
    }

    public static boolean checkPermissionsAndInitialize(Context ctx) {
        context = ctx;
        if (hasPermissions()) {
            initialise();
            return true;
        }

        //we can't request permissions from within the service
        return false;
    }

    public static void enable(Context ctx)
    {
        context = ctx;

        if (!enabled)
        {
            if (checkPermissionsAndInitialize(context)) {
                enabled = true;
            }
        }
    }

    public static void disable()
    {
        //stopStreaming();
        destroy();
    }

    public static void startStreaming() {
        if (hapticStreamer == null) {
            hapticStreamer = new DefaultHapticStreamer();
            hapticStreamer.setCallback(new HapticStreamer.HapticStreamerCallback() {
                @Override
                public void onDiscover(String host) {
                    Log.i(TAG, "onDiscover: " + host);

                    hapticStreamer.connect(host);
                }

                @Override
                public void onConnect(String host) {

                }

                @Override
                public void onDisconnect(String host) {

                }
            });
            hapticStreamer.refreshCandidateIps();
        }
    }

    public static void stopStreaming() {
        if (hapticStreamer != null) {
            hapticStreamer.dispose();
            hapticStreamer = null;
        }
    }

    public static void refreshIp() {
        if (hapticStreamer != null) {
            hapticStreamer.refreshCandidateIps();
        }
    }

    public static void frameTick()
    {
        long now = System.currentTimeMillis();
        if (enabled && hasPairedDevice) {
            repeatingHaptics.forEach((key, haptics) -> {
                haptics.forEach((altKey, haptic) -> {
                    if (haptic.level < 10) {
                        if (haptic.started != 0 &&
                            player.isPlaying(haptic.altKey)) {
                            player.turnOff(haptic.altKey);
                        }
                        haptic.started = 0;
                    } else if (haptic.started == 0 ||
                            (((now - haptic.started) > 150) && // don't check whether a haptic is still playing for 150 ms
                            !player.isPlaying(haptic.altKey))) {
                        //If a repeating haptic isn't playing, start it again with last known values
                        float flIntensity = ((haptic.level / 100.0F) * haptic.intensity);

                        if (haptic.positionType != PositionType.Head ||
                                //If the haptic is head based, then only play if it is within a certain FOV
                                (haptic.rotation >= -360 && haptic.rotation <= -315) || (haptic.rotation >= 0 && haptic.rotation <= 45)) {
                            player.submitRegistered(haptic.key, haptic.altKey, flIntensity, haptic.duration, haptic.rotation, 0);
                            haptic.started = now;
                        }
                    }
                });
            });
        }
        else
        {
            repeatingHaptics.clear();
        }
    }

    /*
       position values:
           0 - Will play on vest and both arms if tactosy tact files present for both
           1 - Will play on (left) vest and on left arm only if tactosy tact files present for left
           2 - Will play on (right) vest and on right arm only if tactosy tact files present for right
           3 - Will play on head only (if present)
           4 - Will play on all devices (that have a pattern defined for them)

       flag values:
           0 - No flags set
           1 - Indicate this is a looping effect that should play repeatedly until stopped

       intensity:
           0 - 100

       angle:
           0 - 360

       yHeight:
           -0.5 - 0.5
    */
    public static void playHaptic(String application, String event, int position, int flags, float intensity, float angle, float yHeight)
    {
        if (enabled && hasPairedDevice) {

            if (ignoreList.contains(event))
            {
                return;
            }

            String key = getHapticEventKey(application, event);

            //Log.v(TAG, event);

            //Special rumble effect that changes intensity per frame
            if (key.contains("rumble"))
            {
                {
                    float highDuration = angle;

                    List<DotPoint> vector = new Vector<>();
                    int flipflop = 0;
                    for (int d = 0; d < 20; d += 4) // Only select every other dot
                    {
                        vector.add(new DotPoint(d + flipflop, (int) intensity));
                        vector.add(new DotPoint(d + 2 + flipflop, (int) intensity));
                        flipflop = 1 - flipflop;
                    }

                    if (key.contains("front")) {
                        player.submitDot("rumble_front", PositionType.VestFront, vector, (int) highDuration);
                    }
                    else {
                        player.submitDot("rumble_back", PositionType.VestBack, vector, (int) highDuration);
                    }
                }
            }
            else if (applicationEventToPatternKeyMap.get(application).containsKey(key)) {
                Vector<Haptic> haptics = applicationEventToPatternKeyMap.get(application).get(key);
                for (Haptic haptic : haptics) {

                    //Don't allow a haptic to interrupt itself if it is already playing
                    if (player.isPlaying(haptics.get(0).altKey)) {
                        return;
                    }

                    //The following groups play at full intensity
                    if (haptic.group.compareTo("environment") == 0) {
                        intensity = 100;
                    }

                    {
                        float flIntensity = ((intensity / 100.0F) * haptic.intensity);
                        float flAngle = angle;
                        float flDuration = haptic.duration;

                        if (position > 0)
                        {
                            BhapticsManager manager = BhapticsModule.getBhapticsManager();

                            //If playing left position and haptic type is right, don;t play that one
                            if (position == 1)
                            {
                                if (haptic.positionType == PositionType.ForearmR ||
                                        haptic.positionType == PositionType.Right) {
                                    continue;
                                }
                            }

                            //If playing right position and haptic type is left, don;t play that one
                            if (position == 2)
                            {
                                if (haptic.positionType == PositionType.ForearmL ||
                                        haptic.positionType == PositionType.Left) {
                                    continue;
                                }
                            }

                            //Are we playing a "head only" pattern?
                            if (position == 3 &&
                                    (haptic.positionType != PositionType.Head || !manager.isDeviceConnected(BhapticsManager.DeviceType.Head)))
                            {
                                continue;
                            }

                            if (haptic.positionType == PositionType.Head) {
                                //Is this a "don't play on head" effect?
                                if (position < 3) {
                                    continue;
                                }

                                //Zero angle for head
                                flAngle = 0;
                            }
                        }

                        //Special values for heartbeat
                        if (haptic.group.compareTo("health") == 0)
                        {
                            //The worse condition we are in, the faster the heart beats!
                            float health = intensity;
                            flDuration = 1.0f - (0.4f * ((40 - health) / 40));
                            flIntensity = 1.0f;
                            flAngle = 0;
                        }

                        //If this is a repeating event, then add to the set to play in begin frame
                        if (flags == 1)
                        {
                            Haptic repeatingHaptic = new Haptic(haptic);

                            if (haptic.directional) {
                                repeatingHaptic.rotation = flAngle;
                            }

                            repeatingHaptic.level = intensity;

                            if (!repeatingHaptics.containsKey(key))
                            {
                                Map<String, Haptic> v = new HashMap<>();
                                v.put(repeatingHaptic.altKey, repeatingHaptic);
                                repeatingHaptics.put(key, v);
                            }
                            else
                            {
                                Map<String, Haptic> v = repeatingHaptics.get(key);
                                if (!v.containsKey(repeatingHaptic.altKey)) {
                                    v.put(repeatingHaptic.altKey, repeatingHaptic);
                                }
                            }
                        }
                        else if (flIntensity > 0)
                        {
                            player.submitRegistered(haptic.key, haptic.altKey, flIntensity, flDuration, flAngle, yHeight);
                        }
                    }
                }
            }
            else
            {
                Log.v(TAG, "Unknown Event: " + event);
                ignoreList.add(event);
            }
        }
    }

    private static String getHapticEventKey(String application, String event) {
        String key = event.toLowerCase();
        
        //Game specific pattern mapping
        if (application.compareTo("Doom3Quest") == 0) {
            if (key.contains("melee")) {
                if (key.contains("right")) {
                    key = "melee_right";
                } else {
                    key = "melee_left";
                }
            } else if (key.contains("damage")) {
                if (key.contains("bullet") ||
                        key.contains("splash") ||
                        key.contains("cgun")) {
                    key = "bullet";
                } else if (key.contains("fireball") ||
                        key.contains("rocket") ||
                        key.contains("explode")) {
                    key = "fireball"; // Just re-use this one
                } else if (key.contains("noair") || key.contains("gasp")) {
                    key = "noair";
                } else if (key.contains("shotgun")) {
                    key = "shotgun";
                } else if (key.contains("fall")) {
                    key = "fall";
                }
            } else if (key.contains("door") || key.contains("panel") || key.contains("silver_sliding")) {
                key = "doorslide";
            } else if (key.contains("lift")) {
                if (key.contains("up")) {
                    key = "liftup";
                } else if (key.contains("down")) {
                    key = "liftdown";
                }
            } else if (key.contains("elevator")) {
                key = "machine";
            } else if (key.contains("entrance_scanner") || key.contains("scanner_rot1s")) {
                key = "scan";
            } else if (key.contains("decon_started")) {
                key = "decontaminate";
            } else if (key.contains("spark")) {
                key = "spark";
            } else if (key.contains("flames") || key.contains("propane") || key.contains("burning")) {
                key = "flame_loop";
            } else if (key.contains("steam")) {
                if (key.contains("blast") || key.contains("shot") || key.contains("chuff")) {
                    key = "steam_blast";
                } else {
                    key = "steam_loop";
                }
            } else if (key.contains("player") && key.contains("jump")) {
                key = "jump_start";
            } else if (key.contains("player") && key.contains("land")) {
                key = "jump_landing";
            }
        }

        return key;
    }

    public static void stopHaptic(String application, String event) {

        if (enabled && hasPairedDevice) {

            String key = getHapticEventKey(application, event);

            if (repeatingHaptics.containsKey(key))
            {
                repeatingHaptics.get(key).forEach((altKey, haptic) -> {
                    player.turnOff(altKey);
                });

                repeatingHaptics.remove(key);
            }
        }
    }

    public static void updateRepeatingHaptic(String application, String event, float intensity, float angle) {

        if (enabled && hasPairedDevice) {

            if (ignoreList.contains(event))
            {
                return;
            }

            String key = getHapticEventKey(application, event);

            if (repeatingHaptics.containsKey(key))
            {
                repeatingHaptics.get(key).forEach((altKey, haptic) -> {
                    if (haptic.directional) {
                        haptic.rotation = angle;
                    }

                    haptic.level = intensity;
                });
            }
        }
    }

    public static String read(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (is, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            return textBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "read: ", e);
        }
        return null;
    }

    public static void scanIfNeeded() {
        BhapticsManager manager = BhapticsModule.getBhapticsManager();

        List<BhapticsDevice> deviceList = manager.getDeviceList();
        for (BhapticsDevice device : deviceList) {
            if (device.isPaired()) {
                hasPairedDevice = true;
                break;
            }
        }

        if (hasPairedDevice) {
            manager.scan();

            manager.addBhapticsManageCallback(new BhapticsManagerCallback() {
                @Override
                public void onDeviceUpdate(List<BhapticsDevice> list) {

                }

                @Override
                public void onScanStatusChange(boolean b) {

                }

                @Override
                public void onChangeResponse() {

                }

                @Override
                public void onConnect(String s) {
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            manager.ping(s);
                        }
                        catch (Throwable e) {
                        }
                    });
                    t.start();
                }

                @Override
                public void onDisconnect(String s) {

                }
            });

        }

        //causing a crash at the moment?!
        //startStreaming();
    }
}
