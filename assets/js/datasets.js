// NASA Earth Information Center - Dataset Manifest
window.EARTH_DATASETS = {
  "appName": "NASA Earth Jukebox",
  "version": "1.1.0",
  "missionStatement": "Translating NASA's Earth Information Center visualizations into real-time, dynamic, multi-sensory soundscapes. Making planetary science accessible through sight, sound, and touch.",
  "officialNasaResources": [
    {
      "id": "eic-db",
      "name": "Earth Information Center (EIC) Content Database",
      "url": "https://svs.gsfc.nasa.gov/gallery/earth-information-center/",
      "type": "Primary Database",
      "description": "Visuals developed by the Scientific Visualization Studio (SVS) used in EIC dashboards."
    },
    {
      "id": "svs-13786",
      "name": "Data Sonification: Caldwell 73",
      "url": "https://svs.gsfc.nasa.gov/13786/",
      "type": "NASA Sonification Framework",
      "description": "Demonstrates how NASA translates visual attributes (luminosity, x/y spatial coordinates) into pitch and volume."
    },
    {
      "id": "svs-5313",
      "name": "Change in Night Lights between 2012 and 2023 (EIC Version)",
      "url": "https://svs.gsfc.nasa.gov/5313/",
      "type": "SVS Visualization",
      "description": "Tracks urban illumination. High contrast testing ground mapping intensity to audio tempo or volume."
    },
    {
      "id": "svs-3912",
      "name": "Global Sea Surface Currents and Temperature",
      "url": "https://svs.gsfc.nasa.gov/3912/#media_group_349716",
      "type": "SVS Visualization",
      "description": "Fluid movement of ocean currents alongside thermal gradients for continuous evolving soundscapes."
    },
    {
      "id": "svs-5669",
      "name": "GEOS Aerosols",
      "url": "https://svs.gsfc.nasa.gov/5669/",
      "type": "SVS Visualization",
      "description": "Monitors paths of smoke, dust, and particulate matter for multi-track acoustic experimentation."
    },
    {
      "id": "svs-5533",
      "name": "Air Quality Model Runs",
      "url": "https://svs.gsfc.nasa.gov/5533/",
      "type": "SVS Visualization",
      "description": "Downloadable frames for algorithmically converting color palettes and pixel densities into sound profiles."
    },
    {
      "id": "svs-5425",
      "name": "Perpetual Ocean 2: Western Boundary Currents",
      "url": "https://svs.gsfc.nasa.gov/5425/",
      "type": "SVS Visualization",
      "description": "High contrast oceanic boundary current maps for aligning visual shifts with musical scales."
    },
    {
      "id": "svs-5196",
      "name": "DYAMOND Global Carbon Dioxide",
      "url": "https://svs.gsfc.nasa.gov/5196/",
      "type": "SVS Visualization",
      "description": "Global CO2 concentrations over time, serving as primary input for driving interactive sonification."
    },
    {
      "id": "svs-5011",
      "name": "Lightning Events Detected from the ISS 2017-2023",
      "url": "https://svs.gsfc.nasa.gov/5011/",
      "type": "SVS Visualization",
      "description": "Punctuated sonification where percussive elements correspond to rapid spaceborne lightning flashes."
    },
    {
      "id": "svs-4960",
      "name": "A 3D View of an Atmospheric River from an Earth System Model",
      "url": "https://svs.gsfc.nasa.gov/4960/",
      "type": "SVS Visualization",
      "description": "Maps the density and geographic spread of moisture into shifting musical landscapes."
    },
    {
      "id": "svs-4950",
      "name": "GEDI Forest Height",
      "url": "https://svs.gsfc.nasa.gov/4950/",
      "type": "SVS Visualization",
      "description": "Global maps of canopy elevation for mapping color spectrums to dynamic audio parameters."
    }
  ],
  "datasets": [
    {
      "id": "global-temperature",
      "title": "GEOS-FP Near-Surface Temperature",
      "subtitle": "NASA SVS 5147: Goddard Earth Observing System 2m Temperature",
      "nasaResourceUrl": "https://svs.gsfc.nasa.gov/5147/",
      "nasaSvsId": "SVS-5147",
      "category": "Atmospheric Temperature & Heat Waves",
      "description": "This satellite observation visualization reveals the global surface air thermal distribution sampled at 2 meters above Earth. The metric spans an extreme temperature range from -40\u00b0C (-40\u00b0F) in severe polar cold up to +50\u00b0C (+122\u00b0F) in intense desert heatwaves. The colorbar maps this thermal gradient progressively: deep purple and dark indigo signify freezing polar zones (-40\u00b0C to -10\u00b0C) over Antarctica, Greenland, and Arctic ice sheets; cyan and soft green mark cool to temperate mid-latitude zones (0\u00b0C to +15\u00b0C); vibrant yellow and warm orange indicate tropical and sub-tropical daytime warmth (+20\u00b0C to +35\u00b0C); while dark crimson, maroon, and white-hot cores highlight dangerous extreme heatwaves exceeding +40\u00b0C to +50\u00b0C across the Sahara Desert, Persian Gulf, and central Australia.",
      "sonificationProfile": {
        "scale": "Dorian & Microtonal Tension",
        "baseFrequency": "110 Hz (A2)",
        "timbre": "Warm atmospheric synth pad reflecting global 2-meter air temperature gradients",
        "mappingRule": "2m surface air temperature maps directly to audio pitch and acoustic harmonic density.",
        "spatialMode": "Polar-orbiting scanline sweeps from 90\u00b0N to 90\u00b0S."
      },
      "frames": [
        {
          "frameIndex": 0,
          "filename": "geos_near_surface_temp_5147.png",
          "label": "GEOS-FP Near-Surface Temperature Field",
          "value": "+15.2\u00b0C (Global Model Average)",
          "metricName": "Near-Surface 2m Air Temperature",
          "audioNarrative": "This satellite observation visualization reveals the global surface air thermal distribution sampled at 2 meters above Earth. The metric spans an extreme temperature range from -40\u00b0C (-40\u00b0F) in severe polar cold up to +50\u00b0C (+122\u00b0F) in intense desert heatwaves. The colorbar maps this thermal gradient progressively: deep purple and dark indigo signify freezing polar zones (-40\u00b0C to -10\u00b0C) over Antarctica, Greenland, and Arctic ice sheets; cyan and soft green mark cool to temperate mid-latitude zones (0\u00b0C to +15\u00b0C); vibrant yellow and warm orange indicate tropical and sub-tropical daytime warmth (+20\u00b0C to +35\u00b0C); while dark crimson, maroon, and white-hot cores highlight dangerous extreme heatwaves exceeding +40\u00b0C to +50\u00b0C across the Sahara Desert, Persian Gulf, and central Australia.",
          "dominantFreqRange": "110 Hz - 880 Hz",
          "intensity": 0.85
        }
      ]
    },
    {
      "id": "near-surface-wind",
      "title": "GEOS-FP Near-Surface Wind Speed",
      "subtitle": "NASA SVS 5148: Global 10-Meter Wind & Storm Systems",
      "nasaResourceUrl": "https://svs.gsfc.nasa.gov/5148/",
      "nasaSvsId": "SVS-5148",
      "category": "Atmospheric Dynamics & Wind Energy",
      "description": "This atmospheric visualization maps surface wind velocity sampled at 10 meters above Earth, illustrating planetary wind streams, cyclonic storms, and frontal boundaries. The metric measures wind speed spanning from 0 m/s (Calm) up to 35+ m/s (Severe Gale and Hurricane-force gusts, exceeding 70 knots). The colorbar interprets aerodynamic intensity: dark indigo and navy blue represent calm to light air (0 to 5 m/s) typically sheltered over landmasses; cyan and emerald green indicate moderate to fresh breezes (6 to 14 m/s) driving oceanic trade winds; bright yellow and amber orange mark strong, near-gale wind corridors (15 to 24 m/s); and vibrant magenta, violet, and bright white streaks highlight dangerous storm tracks, oceanic jet streams, and roaring circumpolar westerly gale belts surpassing 25 to 35+ m/s.",
      "sonificationProfile": {
        "scale": "Dorian Mode / Aerodynamic Resonances (D, E, F, G, A, B, C)",
        "baseFrequency": "146.83 Hz (D3)",
        "timbre": "Atmospheric vortex wind rumbles, gale-force noise sweeps, and fluctuating aerodynamic turbulence filters",
        "mappingRule": "10-meter wind velocity (m/s) controls white noise filter cutoff and low-frequency turbulence modulations; oceanic high-wind belts generate resonant wind whistle harmonics.",
        "spatialMode": "Global storm tracks sweep longitudinally from West to East."
      },
      "frames": [
        {
          "frameIndex": 0,
          "filename": "geos_near_surface_wind_5148.png",
          "label": "Global 10m Wind Speed Field",
          "value": "18.4 m/s (35.8 kts)",
          "metricName": "10-Meter Wind Velocity",
          "audioNarrative": "This atmospheric visualization maps surface wind velocity sampled at 10 meters above Earth, illustrating planetary wind streams, cyclonic storms, and frontal boundaries. The metric measures wind speed spanning from 0 m/s (Calm) up to 35+ m/s (Severe Gale and Hurricane-force gusts, exceeding 70 knots). The colorbar interprets aerodynamic intensity: dark indigo and navy blue represent calm to light air (0 to 5 m/s) typically sheltered over landmasses; cyan and emerald green indicate moderate to fresh breezes (6 to 14 m/s) driving oceanic trade winds; bright yellow and amber orange mark strong, near-gale wind corridors (15 to 24 m/s); and vibrant magenta, violet, and bright white streaks highlight dangerous storm tracks, oceanic jet streams, and roaring circumpolar westerly gale belts surpassing 25 to 35+ m/s.",
          "dominantFreqRange": "60 Hz - 2800 Hz",
          "intensity": 0.72
        }
      ]
    },
    {
      "id": "geos-precipitation-clouds",
      "title": "GEOS-FP Precipitation and Clouds",
      "subtitle": "NASA SVS 5149: Rain, Snow & Cloud Optical Thickness",
      "nasaResourceUrl": "https://svs.gsfc.nasa.gov/5149/",
      "nasaSvsId": "SVS-5149",
      "category": "Hydrological Cycle & Storm Hazards",
      "description": "This satellite imagery overlays active precipitation rates atop atmospheric cloud optical thickness, pinpointing severe convective storm systems and monsoon troughs. The metric measures precipitation intensity spanning from light drizzle at 0.1 mm/hr up to flash flood cloudbursts exceeding 30 mm/hr. The colorbar maps rainfall severity against cloud cover: the monochrome white and translucent gray background shows cloud optical depth, where brighter white indicates denser cloud cover; cool aqua and soft green show light, steady rainfall (0.1 to 2.5 mm/hr); bright yellow and orange indicate moderate to heavy convective downpours (5 to 15 mm/hr); while intense red, crimson, and deep violet cores demarcate severe storm cells, tropical cyclones, and deluge rain bands exceeding 20 to 30+ mm/hr capable of triggering catastrophic flash flooding.",
      "sonificationProfile": {
        "scale": "Aeolian Mode / Hydro-Acoustic Resonance (C, D, Eb, F, G, Ab, Bb)",
        "baseFrequency": "130.81 Hz (C3)",
        "timbre": "Granular rain trickle textures, low-frequency atmospheric thunder rumbles, and resonant cloud filter sweeps",
        "mappingRule": "Precipitation rate (mm/hr) drives droplet density and granular raindrop strikes; cloud optical thickness modulates high-cut atmospheric absorption filters.",
        "spatialMode": "Atmospheric rivers flow eastwards across the Pacific into continental coastal zones."
      },
      "frames": [
        {
          "frameIndex": 0,
          "filename": "geos_precipitation_clouds_5149.png",
          "label": "Total Precipitation & Cloud Optical Depth",
          "value": "24.6 mm/hr Severe Storm Band",
          "metricName": "Precipitation Intensity",
          "audioNarrative": "This satellite imagery overlays active precipitation rates atop atmospheric cloud optical thickness, pinpointing severe convective storm systems and monsoon troughs. The metric measures precipitation intensity spanning from light drizzle at 0.1 mm/hr up to flash flood cloudbursts exceeding 30 mm/hr. The colorbar maps rainfall severity against cloud cover: the monochrome white and translucent gray background shows cloud optical depth, where brighter white indicates denser cloud cover; cool aqua and soft green show light, steady rainfall (0.1 to 2.5 mm/hr); bright yellow and orange indicate moderate to heavy convective downpours (5 to 15 mm/hr); while intense red, crimson, and deep violet cores demarcate severe storm cells, tropical cyclones, and deluge rain bands exceeding 20 to 30+ mm/hr capable of triggering catastrophic flash flooding.",
          "dominantFreqRange": "80 Hz - 3500 Hz",
          "intensity": 0.85
        }
      ]
    },
    {
      "id": "geos-near-surface-humidity",
      "title": "GEOS-FP Near-Surface Humidity",
      "subtitle": "NASA SVS 5150: Specific Humidity (Q2M) in g/kg",
      "nasaResourceUrl": "https://svs.gsfc.nasa.gov/5150/",
      "nasaSvsId": "SVS-5150",
      "category": "Atmospheric Moisture & Water Vapor Dynamics",
      "description": "This atmospheric moisture visualization displays near-surface specific humidity, or Q2M, measuring the actual mass of water vapor per kilogram of air. The metric spans from bone-dry conditions at 0 g/kg up to hyper-saturated tropical moisture corridors exceeding 24 g/kg. The colorbar illustrates atmospheric water vapor content: pale desert yellow and light tan signify arid, moisture-starved air masses (0 to 3 g/kg) characteristic of subtropical deserts and polar ice; mint green and light cyan denote moderate continental humidity (4 to 10 g/kg); rich turquoise and cobalt blue highlight deep moisture transport (12 to 18 g/kg); and dark indigo to saturated violet plumes trace atmospheric rivers and tropical convergence zones surpassing 20 to 24+ g/kg that supply fuel for cyclonic storms and heavy rainfall.",
      "sonificationProfile": {
        "scale": "Mixolydian Mode / Vaporous Harmonics (G, A, B, C, D, E, F)",
        "baseFrequency": "196.00 Hz (G3)",
        "timbre": "Warm oceanic vapor drones, shimmering moisture resonance, and lush ambient chorus filtering",
        "mappingRule": "Specific humidity Q2M (g/kg) controls chorus depth, sub-surface moisture resonance, and shimmer harmonic overtones; arid zones trigger stark, dry acoustic plucks.",
        "spatialMode": "Ocean evaporation plumes track tropical atmospheric moisture corridors West to East."
      },
      "frames": [
        {
          "frameIndex": 0,
          "filename": "geos_near_surface_humidity_5150.png",
          "label": "Global Specific Humidity Q2M Field",
          "value": "16.8 g/kg Tropical Moisture Zone",
          "metricName": "Specific Humidity (Q2M)",
          "audioNarrative": "This atmospheric moisture visualization displays near-surface specific humidity, or Q2M, measuring the actual mass of water vapor per kilogram of air. The metric spans from bone-dry conditions at 0 g/kg up to hyper-saturated tropical moisture corridors exceeding 24 g/kg. The colorbar illustrates atmospheric water vapor content: pale desert yellow and light tan signify arid, moisture-starved air masses (0 to 3 g/kg) characteristic of subtropical deserts and polar ice; mint green and light cyan denote moderate continental humidity (4 to 10 g/kg); rich turquoise and cobalt blue highlight deep moisture transport (12 to 18 g/kg); and dark indigo to saturated violet plumes trace atmospheric rivers and tropical convergence zones surpassing 20 to 24+ g/kg that supply fuel for cyclonic storms and heavy rainfall.",
          "dominantFreqRange": "110 Hz - 3200 Hz",
          "intensity": 0.78
        }
      ]
    }
  ]
};
