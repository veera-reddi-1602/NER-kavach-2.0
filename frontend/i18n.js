/**
 * NER KAVACH 3.0 — Multi-Language Localization Engine (i18n)
 * Supported Languages:
 * - en: English (Default)
 * - hi: हिन्दी (Hindi)
 * - as: অসমীয়া (Assamese - Northeast Regional Lingua Franca)
 * - bn: বাংলা (Bengali - Northeast Regional)
 */

const I18N_LANGUAGES = [
  { code: 'en', name: 'English', native: 'English', icon: '🇬🇧' },
  { code: 'hi', name: 'Hindi', native: 'हिन्दी', icon: '🇮🇳' },
  { code: 'as', name: 'Assamese', native: 'অসমীয়া', icon: '🏔️' },
  { code: 'bn', name: 'Bengali', native: 'বাংলা', icon: '🌿' }
];

const I18N_TRANSLATIONS = {
  en: {
    // App Header & Modes
    app_title: "NER KAVACH",
    app_subtitle: "AI Early Warning System",
    online_auto: "Online Auto",
    offline_auto: "Offline Auto",
    news_tab: "📰 News",
    status_online: "ONLINE",
    status_offline: "OFFLINE",
    lang_btn_label: "English",
    lang_modal_title: "Select Language / भाषा चुनें",
    lang_modal_subtitle: "Choose your preferred language for landslide early warning & civil defense",
    lang_continue_btn: "Continue to App",

    // Top 4 KPI Cards
    kpi_high_risk_title: "High Risk Sectors",
    kpi_high_risk_sub: "Tawang • Bomdila • Anini ℹ️",
    kpi_roads_title: "Highway Blockades",
    kpi_roads_sub: "NH-13 (Rupa) • NH-313 ℹ️",
    kpi_shelters_title: "Army & Relief Shelters",
    kpi_shelters_sub: "4,900 Bed Capacity ℹ️",
    kpi_mesh_title: "Arunachal Gateway",
    kpi_mesh_sub: "5/5 Nodes Active (868MHz) ℹ️",
    active_tag: "ACTIVE",
    blocked_tag: "BLOCKED",
    critical_tag: "CRITICAL",
    lora_mesh_tag: "LORA MESH",

    // Bottom Navigation Bar
    nav_ai_twin: "AI Twin",
    nav_telemetry: "Telemetry",
    nav_alerts: "Alerts",
    nav_evacuation: "Evacuation",
    nav_report: "Report",

    // AI Twin Tab
    sector_select_title: "Select Arunachal Sector",
    sector_districts: "5 Districts",
    xgboost_twin_title: "Geotechnical XGBoost AI Digital Twin",
    xgboost_twin_sub: "Calibrated for Arunachal Eastern Himalayan Rock Formations",
    hazard_high: "HIGH HAZARD",
    hazard_moderate: "MODERATE HAZARD",
    hazard_low: "LOW HAZARD",
    confidence_band: "Confidence Band: 87% ± 5% (Arunachal Physics)",
    rainfall_slider_label: "24h Cumulative Rainfall",
    soil_moisture_slider_label: "Soil Moisture Saturation",
    slope_gradient_slider_label: "Slope Gradient Steepness",
    insar_velocity_slider_label: "InSAR Ground Velocity",

    // Telemetry Tab
    telemetry_section_title: "Sector Telemetry & Solar Nodes",
    solar_batt_label: "Solar Batt",
    stat_24h_rainfall: "24h Rainfall",
    stat_soil_moisture: "Soil Moisture",
    stat_slope_gradient: "Slope Gradient",
    stat_insar_velocity: "InSAR Velocity",
    highway_status_label: "NH-13",
    highway_blocked_val: "BLOCKED (Baisakhi)",
    shelter_status_label: "Shelter",
    shelter_status_val: "Tawang Army Disaster Center (850/1200 beds)",
    
    // Evacuation Route & Radar Operations
    safe_evac_card_title: "Safe Evacuation to Army Base Camp",
    safe_evac_card_desc: "Computes real-time safest route from your sector to the fortified Base Camp with terrain landmarks.",
    btn_find_route: "🧭 Find Safest Route to Base Camp",
    btn_view_fullscreen: "🧭 View Full-Screen Route Map",
    btn_computing_route: "⏳ Computing Safest Terrain Corridor...",
    btn_recalculate_route: "🔄 Recalculate Route",
    return_to_telemetry: "← Return to Telemetry",
    fs_route_title: "Safest Evacuation Route",
    fs_stat_distance: "Total Distance",
    fs_stat_travel_time: "Travel Time",
    fs_stat_safety_index: "Safety Index",
    fs_stat_blocked_road: "Blocked Road",
    fs_stat_avoided: "Avoided",
    fs_stat_verified: "98.4% Safe Verified",
    fs_landmarks_title: "📍 Route Landmarks & Evacuation Waypoints",
    
    // Radar Scanning Modal
    radar_modal_title: "FINDING SAFEST BASE CAMP ROUTE",
    radar_modal_sub: "Arunachal Geotechnical Physics & Risk-Cost Corridor Pathfinding",
    radar_step1_status: "Querying SRTM 30m Digital Elevation Model & InSAR slope gradients...",
    radar_step1_log: "🛰️ [01/05] Querying SRTM 30m Digital Elevation Model & InSAR slope gradients for Tawang Sector...",
    radar_step2_status: "Evaluating Antecedent Rainfall & Soil Saturation...",
    radar_step2_log: "🌧️ [02/05] Ingesting AWS real-time rainfall & soil pore-pressure saturation (Tawang Sector axis)...",
    radar_step3_status: "Flagging Geotechnical Slope Hazards & Blocked Corridors...",
    radar_step3_log: "⛔ [03/05] Detecting active debris flow blockades... NH-13 Baisakhi flagged impassable!",
    radar_step4_status: "Synthesizing Multi-Criteria Dijkstra Risk-Weighted Path...",
    radar_step4_log: "🧭 [04/05] Calculating Dijkstra risk-weighted multi-criteria pathfinding to Base Camp...",
    radar_step5_status: "Finalizing Safe Corridor via Sela Tunnel Bypass...",
    radar_step5_log: "✅ [05/05] Optimal corridor verified via Sela Tunnel Bypass (98.4% Safety Index)!",

    // Offline Monitor & Gateway
    offline_monitor_title: "LANDSLIDE MONITOR",
    offline_mode_tag: "⚠ OFFLINE MODE",
    offline_internet_label: "Internet:",
    offline_internet_val: "🔴 Not Available",
    offline_weather_label: "Weather:",
    offline_weather_val: "Using cached forecast",
    offline_last_update_label: "Last weather update:",
    offline_gateway_status_disconnected: "LoRa Mesh: Disconnected (Offline)",
    offline_gateway_status_scanning: "Scanning 868MHz for Local Gateway...",
    offline_gateway_status_connected: "LoRa Mesh: Connected to Gateway (192.168.1.1)",
    btn_search_gateway: "📡 Look for Local Gateway Connection",
    btn_connecting_gateway: "🔄 Connecting to Gateway...",
    btn_connected_gateway: "✅ Connected to Local Gateway",
    offline_cached_data_title: "OFFLINE CACHED GEOTECHNICAL RISK",

    // Alerts Tab
    alerts_header: "Early Warning Bulletins & Civil Defense",
    alert_critical_title: "EVACUATION ADVISORY: TAWANG SECTOR",
    alert_critical_desc: "Geotechnical sensors indicate active slope failure probability > 78%. Immediate evacuation advised to Army Base Camp via Sela Tunnel bypass.",
    alert_siren_btn: "🚨 Trigger Emergency Siren",
    alert_ndrf_hotline: "NDRF Helpline: 1078 • Army Ops: +91-3772-222222",

    // Evacuation Tab
    evac_header: "Designated Relief Camps & Bed Capacities",
    evac_shelter_1: "Tawang Army Disaster Relief Center",
    evac_shelter_2: "Bomdila Civil Evacuation Stadium",
    evac_shelter_3: "Anini Community Emergency Shelter",
    evac_call_btn: "📞 Call Camp Commander",
    evac_nav_btn: "🧭 Navigate to Camp",

    // Community Report Tab
    report_header: "Community Hazard Reporting",
    report_sub: "Submit real-time ground observations directly to District Emergency Operations Centre (DEOC) & local gateway mesh.",
    report_type_label: "Hazard Type",
    report_type_debris: "Active Debris Flow / Rockfall",
    report_type_crack: "Ground Tension Cracks / Creep",
    report_type_subsidence: "Road Subsidence / Sinking",
    report_location_label: "Sector / Location",
    report_desc_label: "Description & Details",
    report_desc_placeholder: "Describe visible rock movements, road blockage, tree tilt, or stream water mudding...",
    report_photo_btn: "📷 Attach Observation Photo",
    report_submit_btn: "📤 Submit Hazard Report",
    report_offline_note: "ℹ️ Offline Mode: Reports are queued securely and transmitted via Local Gateway LoRa Mesh.",

    // Weather News & Local Mobiles Alert
    weather_news_title: "Local Weather News & Regional Bulletins",
    weather_news_sub: "Real-time & Offline-Cached Meteorological Advisories (Arunachal Corridor)",
    filter_all_sectors: "All Sectors",
    filter_tawang: "Tawang",
    filter_bomdila: "Bomdila",
    filter_anini: "Anini",
    filter_pasighat: "Pasighat",
    filter_itanagar: "Itanagar",
    offline_cached_badge: "💾 Offline Cached (Available Without Internet)",
    live_synced_badge: "🟢 Live IMD & Satellite Grid Synced",
    btn_sync_weather: "🔄 Refresh Weather Bulletins",
    btn_send_local_mobiles: "Send Msg to Local Mobiles",
    local_mobiles_modal_title: "LOCAL CELL BROADCAST & LORA SMS DISPATCH",
    local_mobiles_modal_sub: "Direct Emergency Alert Transmitted to All Citizen Phones in Sector",
    mobiles_alerted_label: "Mobiles Alerted",
    broadcast_protocol_label: "Protocol",
    delivery_status_label: "Delivery Status",
    btn_dismiss_alert: "Close Receipt",

    // Comparative Risk Bar Chart & 48h Local Weather Prediction
    risk_bar_chart_title: "Comparative Sector Landslide Risk Profile",
    risk_bar_chart_sub: "Real-time XGBoost Threat Scoring across Arunachal Corridors",
    risk_by_place_title: "📊 Risk by Place",
    risk_by_place_sub: "Real-time XGBoost ML Hazard Probability across Arunachal Sectors",
    slope_vs_risk_title: "📈 Slope vs Risk",
    slope_vs_risk_sub: "Dynamic Terrain Slope Gradient vs Landslide Failure Probability",
    critical_threshold_label: "70% Critical Hazard Threshold (Evacuation Trigger)",
    selected_sector_badge: "📍 Selected Sector",
    weather_prediction_title: "Local Weather Office 48h Weather Prediction",
    weather_office_source: "📡 Official Telemetry: Regional Met Centre (RMC Itanagar / IMD Arunachal)",
    past_24h_label: "Past 24 Hours (Recorded Rain)",
    next_24h_label: "Next 24 Hours (Prediction)",
    peak_hazard_window: "Peak Cloudburst Hazard Window",
    weather_station_label: "Local Weather Station",
    now_marker_label: "NOW (Live)",
    cloudburst_forecast_note: "⚠️ Heavy cloudburst surge predicted between +6h and +12h. Soil saturation expected to exceed threshold.",

    // Evacuation Tab Polished Header
    evac_hero_badge: "👨‍👩‍👧 ARUNACHAL DEFENSE PROTOCOL",
    evac_gnn_badge: "⚡ GNN AI ENGINE ACTIVE",
    evac_main_title: "Micro-Evacuation Priority Roster",
    evac_main_desc: "Graph Neural Network ranked vulnerable households across Tawang, Anini, and Bomdila corridors based on terrain proximity, infant/elderly density, and dynamic slope failure thresholds.",
    evac_stat_homes: "5 Priority Homes",
    evac_stat_homes_lbl: "Critical Evacuation",
    evac_stat_shelters: "100% Assigned",
    evac_stat_shelters_lbl: "Fortified Shelters",
    evac_stat_window: "15 Min Window",
    evac_stat_window_lbl: "Safe Transit Margin",

    // Telemetry Tab - Official Situation PDF & Government / District Collector Dispatch
    gov_dispatch_badge: "🤖 AUTONOMOUS AI SENTINEL",
    gov_dispatch_title: "AI Disaster Prediction Dossier to Collector",
    gov_dispatch_sub: "Autonomous Edge-AI synthesizes multi-modal telemetry, 48h cloudburst models, and prescriptive directives for proactive administrative intervention.",
    btn_gov_dispatch: "⚡ Transmit AI Prediction Dossier to District Collector",
    dispatch_step1: "🧠 Physics-Informed GNN v3.4 ingesting real-time sensor streams & satellite radar...",
    dispatch_step2: "🔮 Deep Learning engine generating 48h cloudburst trajectory & failure probability...",
    dispatch_step3: "📡 Encrypting AI Dossier & broadcasting to District Collector & DEOC via LoRa Mesh...",
    dispatch_step4: "✅ AI Prediction Dossier Delivered to District Collector & Local Government!",
    pdf_modal_title: "AI Early-Warning Disaster Prediction Dossier",
    pdf_modal_sub: "NER-KAVACH Autonomous AI Sentinel • Dispatched to District Collector & Local Administration",
    btn_download_pdf: "📥 Download AI Dossier (PDF)",
    btn_print_pdf: "🖨️ Print / Save as PDF",
    btn_close_pdf: "✖ Close Viewer"
  },

  hi: {
    // App Header & Modes
    app_title: "एनईआर कवच",
    app_subtitle: "एआई पूर्व चेतावनी प्रणाली",
    online_auto: "ऑनलाइन ऑटो",
    offline_auto: "ऑफलाइन ऑटो",
    news_tab: "📰 समाचार",
    status_online: "ऑनलाइन",
    status_offline: "ऑफलाइन",
    lang_btn_label: "हिन्दी",
    lang_modal_title: "भाषा का चयन करें",
    lang_modal_subtitle: "भूस्खलन पूर्व चेतावनी और आपातकालीन आपदा प्रतिक्रिया के लिए अपनी पसंदीदा भाषा चुनें",
    lang_continue_btn: "ऐप में आगे बढ़ें",

    // Top 4 KPI Cards
    kpi_high_risk_title: "उच्च जोखिम सेक्टर",
    kpi_high_risk_sub: "तवांग • बोमडिला • अनि‍नी ℹ️",
    kpi_roads_title: "राजमार्ग रुकावटें",
    kpi_roads_sub: "एनएच-13 (रूपा) • एनएच-313 ℹ️",
    kpi_shelters_title: "सेना व राहत शिविर",
    kpi_shelters_sub: "4,900 बिस्तर क्षमता ℹ️",
    kpi_mesh_title: "अरुणाचल गेटवे",
    kpi_mesh_sub: "5/5 नोड्स सक्रिय (868MHz) ℹ️",
    active_tag: "सक्रिय",
    blocked_tag: "अवरुद्ध",
    critical_tag: "गंभीर",
    lora_mesh_tag: "लोरा मेश",

    // Bottom Navigation Bar
    nav_ai_twin: "एआई ट्विन",
    nav_telemetry: "टेलीमेट्री",
    nav_alerts: "अलर्ट",
    nav_evacuation: "निकासी",
    nav_report: "रिपोर्ट",

    // AI Twin Tab
    sector_select_title: "अरुणाचल सेक्टर चुनें",
    sector_districts: "5 जिले",
    xgboost_twin_title: "जियोटेक्निकल एक्सजीबूस्ट एआई डिजिटल ट्विन",
    xgboost_twin_sub: "पूर्वी हिमालयी चट्टानों के अरुणाचल भौतिकी अनुसार कैलिब्रेटेड",
    hazard_high: "उच्च जोखिम (खतरा)",
    hazard_moderate: "मध्यम जोखिम",
    hazard_low: "कम जोखिम (सुरक्षित)",
    confidence_band: "विश्वसनीयता दायरा: 87% ± 5% (अरुणाचल भौतिकी)",
    rainfall_slider_label: "24 घंटे की कुल वर्षा",
    soil_moisture_slider_label: "मिट्टी की नमी संतृप्ति",
    slope_gradient_slider_label: "ढलान की प्रवणता (तीव्रता)",
    insar_velocity_slider_label: "इनसार भू-गति वेग",

    // Telemetry Tab
    telemetry_section_title: "सेक्टर टेलीमेट्री व सौर नोड्स",
    solar_batt_label: "सौर बैटरी",
    stat_24h_rainfall: "24 घंटे वर्षा",
    stat_soil_moisture: "मिट्टी नमी",
    stat_slope_gradient: "ढलान कोण",
    stat_insar_velocity: "इनसार गति",
    highway_status_label: "एनएच-13",
    highway_blocked_val: "अवरुद्ध (बैसाखी)",
    shelter_status_label: "राहत शिविर",
    shelter_status_val: "तवांग सैन्य आपदा केंद्र (850/1200 बिस्तर)",
    
    // Evacuation Route & Radar Operations
    safe_evac_card_title: "सेना बेस कैंप तक सुरक्षित निकासी",
    safe_evac_card_desc: "भू-भाग के प्रमुख स्थलों के साथ आपके सेक्टर से सुरक्षित बेस कैंप तक का वास्तविक मार्ग खोजता है।",
    btn_find_route: "🧭 बेस कैंप हेतु सबसे सुरक्षित मार्ग खोजें",
    btn_view_fullscreen: "🧭 पूर्ण स्क्रीन मार्ग मानचित्र देखें",
    btn_computing_route: "⏳ सुरक्षित मार्ग की गणना की जा रही है...",
    btn_recalculate_route: "🔄 मार्ग पुनः खोजें",
    return_to_telemetry: "← टेलीमेट्री पर वापस जाएं",
    fs_route_title: "सबसे सुरक्षित निकासी मार्ग",
    fs_stat_distance: "कुल दूरी",
    fs_stat_travel_time: "यात्रा समय",
    fs_stat_safety_index: "सुरक्षा सूचकांक",
    fs_stat_blocked_road: "अवरुद्ध मार्ग",
    fs_stat_avoided: "बचाव किया गया",
    fs_stat_verified: "98.4% सुरक्षित प्रमाणित",
    fs_landmarks_title: "📍 मार्ग के मुख्य पड़ाव व निकासी स्थल",
    
    // Radar Scanning Modal
    radar_modal_title: "सुरक्षित बेस कैंप मार्ग खोज रहे हैं",
    radar_modal_sub: "अरुणाचल भू-तकनीकी भौतिकी व न्यूनतम-जोखिम मार्ग निर्धारण",
    radar_step1_status: "एसआरटीएम 30मी डिजिटल एलिवेशन मॉडल व इनसार ढलान डेटा जांच रहे हैं...",
    radar_step1_log: "🛰️ [01/05] तवांग सेक्टर हेतु एसआरटीएम 30मी एलिवेशन व इनसार ढलान प्रवणता की जांच...",
    radar_step2_status: "पूर्व वर्षा व मिट्टी संतृप्ति का मूल्यांकन...",
    radar_step2_log: "🌧️ [02/05] वास्तविक वर्षा मापक डेटा एवं मृदा संतृप्ति का विश्लेषण...",
    radar_step3_status: "सक्रिय भूस्खलन मलबे व अवरुद्ध मार्गों की पहचान...",
    radar_step3_log: "⛔ [03/05] सक्रिय मलबे के बहाव का पता चला... एनएच-13 बैसाखी मार्ग अवरुद्ध!",
    radar_step4_status: "डाइकस्ट्रा एल्गोरिदम द्वारा बहु-मानदंडीय सुरक्षित मार्ग निर्माण...",
    radar_step4_log: "🧭 [04/05] सेना बेस कैंप हेतु डाइकस्ट्रा न्यूनतम-जोखिम मार्ग की गणना...",
    radar_step5_status: "सेला सुरंग बाईपास द्वारा सुरक्षित गलियारा प्रमाणित...",
    radar_step5_log: "✅ [05/05] सेला सुरंग बाईपास द्वारा सर्वोत्तम सुरक्षित मार्ग सत्यापित (98.4% सुरक्षा सूचकांक)!",

    // Offline Monitor & Gateway
    offline_monitor_title: "भूस्खलन निगरानी तंत्र",
    offline_mode_tag: "⚠ ऑफलाइन मोड",
    offline_internet_label: "इंटरनेट:",
    offline_internet_val: "🔴 उपलब्ध नहीं है",
    offline_weather_label: "मौसम:",
    offline_weather_val: "सहेजे गए पूर्वानुमान का उपयोग",
    offline_last_update_label: "अंतिम मौसम अपडेट:",
    offline_gateway_status_disconnected: "लोरा मेश: डिस्कनेक्टेड (ऑफलाइन)",
    offline_gateway_status_scanning: "स्थानीय गेटवे हेतु 868MHz स्कैन किया जा रहा है...",
    offline_gateway_status_connected: "लोरा मेश: स्थानीय गेटवे से कनेक्टेड (192.168.1.1)",
    btn_search_gateway: "📡 स्थानीय गेटवे कनेक्शन खोजें",
    btn_connecting_gateway: "🔄 गेटवे से कनेक्ट हो रहा है...",
    btn_connected_gateway: "✅ स्थानीय गेटवे से कनेक्टेड",
    offline_cached_data_title: "ऑफलाइन सहेजा गया भू-तकनीकी जोखिम",

    // Alerts Tab
    alerts_header: "पूर्व चेतावनी बुलेटिन व नागरिक सुरक्षा",
    alert_critical_title: "तत्काल निकासी परामर्श: तवांग सेक्टर",
    alert_critical_desc: "जियोटेक्निकल सेंसर सक्रिय ढलान टूटने की संभावना > 78% दर्शा रहे हैं। सेला सुरंग बाईपास द्वारा बेस कैंप जाने की तत्काल सलाह दी जाती है।",
    alert_siren_btn: "🚨 आपातकालीन सायरन बजाएं",
    alert_ndrf_hotline: "एनडीआरएफ हेल्पलाइन: 1078 • सैन्य संचालन: +91-3772-222222",

    // Evacuation Tab
    evac_header: "नामित राहत शिविर व बिस्तर क्षमता",
    evac_shelter_1: "तवांग सेना आपदा राहत केंद्र",
    evac_shelter_2: "बोमडिला सिविल निकासी स्टेडियम",
    evac_shelter_3: "अनि‍नी सामुदायिक आपातकालीन केंद्र",
    evac_call_btn: "📞 शिविर कमांडर को कॉल करें",
    evac_nav_btn: "🧭 शिविर का मार्ग देखें",

    // Community Report Tab
    report_header: "सामुदायिक आपदा रिपोर्टिंग",
    report_sub: "जिला आपातकालीन संचालन केंद्र (DEOC) और स्थानीय गेटवे को वास्तविक जमीनी जानकारी भेजें।",
    report_type_label: "आपदा का प्रकार",
    report_type_debris: "सक्रिय मलबा बहाव / चट्टान गिरना",
    report_type_crack: "जमीन में दरारें / भू-धंसाव",
    report_type_subsidence: "सड़क धंसना / टूटना",
    report_location_label: "सेक्टर / स्थान",
    report_desc_label: "विवरण और स्थिति",
    report_desc_placeholder: "दिखने वाली चट्टानी हलचल, मार्ग अवरोध या मटमैले पानी का विवरण लिखें...",
    report_photo_btn: "📷 घटनास्थल की तस्वीर जोड़ें",
    report_submit_btn: "📤 आपदा रिपोर्ट सबमिट करें",
    report_offline_note: "ℹ️ ऑफलाइन मोड: रिपोर्ट सुरक्षित सहेजी गई है और लोकल गेटवे लोरा मेश द्वारा प्रेषित होगी।",

    // Weather News & Local Mobiles Alert
    weather_news_title: "स्थानीय मौसम समाचार व बुलेटिन",
    weather_news_sub: "रीयल-टाइम और ऑफ़लाइन-कैश्ड मौसम विज्ञान चेतावनियां (अरुणाचल प्रदेश)",
    filter_all_sectors: "सभी सेक्टर",
    filter_tawang: "तवांग",
    filter_bomdila: "बोमडिला",
    filter_anini: "अनि‍नी",
    filter_pasighat: "पासीघाट",
    filter_itanagar: "ईटानगर",
    offline_cached_badge: "💾 ऑफ़लाइन कैश्ड (इंटरनेट के बिना भी उपलब्ध)",
    live_synced_badge: "🟢 लाइव आईएमडी और उपग्रह ग्रिड कनेक्टेड",
    btn_sync_weather: "🔄 मौसम बुलेटिन रीफ्रेश करें",
    btn_send_local_mobiles: "स्थानीय मोबाइल पर संदेश भेजें",
    local_mobiles_modal_title: "स्थानीय सेल ब्रॉडकास्ट व लोरा एसएमएस प्रेषण",
    local_mobiles_modal_sub: "सेक्टर के सभी नागरिक मोबाइलों पर सीधा आपातकालीन अलर्ट प्रेषित",
    mobiles_alerted_label: "अलर्ट किए गए मोबाइल",
    broadcast_protocol_label: "प्रोटोकॉल",
    delivery_status_label: "वितरण स्थिति",
    btn_dismiss_alert: "रसीद बंद करें",

    // Comparative Risk Bar Chart & 48h Local Weather Prediction
    risk_bar_chart_title: "तुलनात्मक सेक्टर भूस्खलन जोखिम प्रोफाइल",
    risk_bar_chart_sub: "अरुणाचल के सभी सेक्टरों में रीयल-टाइम एक्सजिबूट एआई जोखिम स्कोरिंग",
    risk_by_place_title: "📊 स्थान अनुसार जोखिम",
    risk_by_place_sub: "अरुणाचल के प्रमुख स्थानों पर रियल-टाइम एआई जोखिम संभावना",
    slope_vs_risk_title: "📈 ढलान बनाम जोखिम",
    slope_vs_risk_sub: "पहाड़ी ढलान प्रवणता बनाम भूस्खलन विफलता संभावना",
    critical_threshold_label: "70% गंभीर जोखिम सीमा (निकासी ट्रिगर)",
    selected_sector_badge: "📍 चयनित सेक्टर",
    weather_prediction_title: "स्थानीय मौसम कार्यालय 48 घंटे का मौसम पूर्वानुमान",
    weather_office_source: "📡 आधिकारिक टेलीमेट्री: क्षेत्रीय मौसम विज्ञान केंद्र (आरएमसी ईटानगर / आईएमडी)",
    past_24h_label: "पिछले 24 घंटे (दर्ज वर्षा)",
    next_24h_label: "अगले 24 घंटे (पूर्वानुमान)",
    peak_hazard_window: "चरम बादल फटने का जोखिम समय",
    weather_station_label: "स्थानीय मौसम स्टेशन",
    now_marker_label: "अभी (लाइव)",
    cloudburst_forecast_note: "⚠️ +6 से +12 घंटे में भारी बादल फटने की चेतावनी। मिट्टी की संतृप्ति सीमा पार करने की आशंका।",

    // Evacuation Tab Polished Header
    evac_hero_badge: "👨‍👩‍👧 अरुणाचल सुरक्षा प्रोटोकॉल",
    evac_gnn_badge: "⚡ जीएनएन एआई इंजन सक्रिय",
    evac_main_title: "माइक्रो-निकासी प्राथमिकता सूची",
    evac_main_desc: "ढलान निकटता, बुजुर्ग/शिशु घनत्व और भूस्खलन विफलता सीमा के आधार पर तवांग, अनि‍नी और बोमडिला के संवेदनशील परिवारों की जीएनएन एआई रैंकिंग।",
    evac_stat_homes: "5 प्राथमिकता घर",
    evac_stat_homes_lbl: "गंभीर निकासी",
    evac_stat_shelters: "100% आवंटित",
    evac_stat_shelters_lbl: "सुरक्षित राहत शिविर",
    evac_stat_window: "15 मिनट का समय",
    evac_stat_window_lbl: "सुरक्षित आवागमन",

    // Telemetry Tab - Official Situation PDF & Government / District Collector Dispatch
    gov_dispatch_badge: "🤖 स्वायत्त एआई संतरी",
    gov_dispatch_title: "जिला कलेक्टर को एआई आपदा भविष्यवाणी डोजियर",
    gov_dispatch_sub: "स्वायत्त एज-एआई सक्रिय प्रशासनिक हस्तक्षेप के लिए मल्टी-मॉडल टेलीमेट्री, 48 घंटे के क्लाउडबर्स्ट मॉडल और निर्देशात्मक उपायों का संकलन करता है।",
    btn_gov_dispatch: "⚡ जिला कलेक्टर को एआई भविष्यवाणी डोजियर भेजें",
    dispatch_step1: "🧠 भौतिकी-सूचित जीएनएन v3.4 रीयल-टाइम सेंसर स्ट्रीम और उपग्रह रडार संसाधित कर रहा है...",
    dispatch_step2: "🔮 डीप लर्निंग इंजन 48 घंटे के क्लाउडबर्स्ट प्रक्षेपवक्र और विफलता संभावना की गणना कर रहा है...",
    dispatch_step3: "📡 एआई डोजियर एन्क्रिप्ट कर लोरा मेश द्वारा जिला कलेक्टर एवं डीईओसी को भेजा जा रहा है...",
    dispatch_step4: "✅ एआई भविष्यवाणी डोजियर जिला कलेक्टर एवं स्थानीय प्रशासन को सफलतापूर्वक प्रेषित!",
    pdf_modal_title: "एआई पूर्व चेतावनी आपदा भविष्यवाणी डोजियर",
    pdf_modal_sub: "एनईआर कवच स्वायत्त एआई संतरी • जिला कलेक्टर एवं स्थानीय प्रशासन को प्रेषित",
    btn_download_pdf: "📥 एआई डोजियर डाउनलोड करें",
    btn_print_pdf: "🖨️ प्रिंट / पीडीएफ सहेजें",
    btn_close_pdf: "✖ विंडो बंद करें"
  },

  as: {
    // App Header & Modes
    app_title: "এনইআৰ কৱচ",
    app_subtitle: "এআই আগতীয়া সতৰ্কবাণী ব্যৱস্থা",
    online_auto: "অনলাইন স্বয়ংক্ৰিয়",
    offline_auto: "অফলাইন স্বয়ংক্ৰিয়",
    news_tab: "📰 বাতৰি",
    status_online: "অনলাইন",
    status_offline: "অফলাইন",
    lang_btn_label: "অসমীয়া",
    lang_modal_title: "ভাষা বাছক",
    lang_modal_subtitle: "ভূমিস্খলনৰ আগতীয়া সতৰ্কবাণী আৰু জৰুৰীকালীন সাহায্যৰ বাবে নিজৰ ভাষা বাছক",
    lang_continue_btn: "এপত আগবাঢ়ক",

    // Top 4 KPI Cards
    kpi_high_risk_title: "উচ্চ বিপদ সংকুল খণ্ড",
    kpi_high_risk_sub: "টাৱাং • বমডিলা • অনি‍নি ℹ️",
    kpi_roads_title: "ৰাজপথ অৱৰোধ",
    kpi_roads_sub: "এনএইচ-১৩ (ৰূপা) • এনএইচ-৩১৩ ℹ️",
    kpi_shelters_title: "সেনা আৰু আশ্ৰয় শিবিৰ",
    kpi_shelters_sub: "৪,৯০০ খন বিছনাৰ সুবিধা ℹ️",
    kpi_mesh_title: "অৰুণাচল গেটৱে",
    kpi_mesh_sub: "৫/৫ নোড সক্ৰিয় (868MHz) ℹ️",
    active_tag: "সক্ৰিয়",
    blocked_tag: "অৱৰুদ্ধ",
    critical_tag: "গুৰুতৰ",
    lora_mesh_tag: "লোৰা মেশ্ব",

    // Bottom Navigation Bar
    nav_ai_twin: "এআই টুইন",
    nav_telemetry: "টেলিমেট্ৰি",
    nav_alerts: "সতৰ্কতা",
    nav_evacuation: "স্থানান্তৰ",
    nav_report: "প্ৰতিবেদন",

    // AI Twin Tab
    sector_select_title: "অৰুণাচলৰ খণ্ড বাছক",
    sector_districts: "৫ খন জিলা",
    xgboost_twin_title: "ভূতাত্বিক এক্সজিবুষ্ট এআই ডিজিটেল টুইন",
    xgboost_twin_sub: "পূৱ হিমালয়ৰ পাহাৰীয়া শিলৰ ভৌতিক স্থিতি অনুসৰি প্ৰস্তুত",
    hazard_high: "উচ্চ বিপদজনক খণ্ড",
    hazard_moderate: "মধ্যমীয়া বিপদ",
    hazard_low: "নিম্ন বিপদ (নিৰাপদ)",
    confidence_band: "নিৰ্ভৰযোগ্যতাৰ সীমা: ৮৭% ± ৫%",
    rainfall_slider_label: "২৪ ঘণ্টাৰ মুঠ বৰষুণ",
    soil_moisture_slider_label: "মাটিৰ আৰ্দ্ৰতাৰ পৰিমাণ",
    slope_gradient_slider_label: "পাহাৰৰ ঢালৰ তীক্ষ্ণতা",
    insar_velocity_slider_label: "ইনচাৰ মাটিৰ গতি বেগ",

    // Telemetry Tab
    telemetry_section_title: "খণ্ড টেলিমেট্ৰি আৰু সৌৰ নোড",
    solar_batt_label: "সৌৰ বেটাৰী",
    stat_24h_rainfall: "২৪ ঘণ্টাৰ বৰষুণ",
    stat_soil_moisture: "মাটিৰ আৰ্দ্ৰতা",
    stat_slope_gradient: "ঢালৰ মাত্ৰা",
    stat_insar_velocity: "ইনচাৰ গতি",
    highway_status_label: "এনএইচ-১৩",
    highway_blocked_val: "অৱৰুদ্ধ (বৈশাখী)",
    shelter_status_label: "আশ্ৰয় শিবিৰ",
    shelter_status_val: "টাৱাং সেনা দুৰ্যোগ কেন্দ্ৰ (৮৫০/১২০০ বিছনা)",
    
    // Evacuation Route & Radar Operations
    safe_evac_card_title: "সেনা বেচ কেম্পলৈ সুৰক্ষিত স্থানান্তৰ",
    safe_evac_card_desc: "আপোনাৰ খণ্ডৰ পৰা সুৰক্ষিত সেনা বেচ কেম্পলৈ পাহাৰীয়া স্থান চিনাক্ত কৰি আটাইতকৈ নিৰাপদ পথ বিচাৰি উলিয়ায়।",
    btn_find_route: "🧭 সুৰক্ষিত বেচ কেম্প পথ বিচাৰক",
    btn_view_fullscreen: "🧭 সম্পূৰ্ণ পৰ্দাৰ মানচিত্ৰ চাওক",
    btn_computing_route: "⏳ নিৰাপদ পথ গণনা কৰি থকা হৈছে...",
    btn_recalculate_route: "🔄 পথ পুনৰ নিৰ্ণয় কৰক",
    return_to_telemetry: "← টেলিমেট্ৰিলৈ ঘূৰি যাওক",
    fs_route_title: "আটাইতকৈ নিৰাপদ স্থানান্তৰ পথ",
    fs_stat_distance: "মুঠ দূৰত্ব",
    fs_stat_travel_time: "যাত্ৰাৰ সময়",
    fs_stat_safety_index: "সুৰক্ষা সূচক",
    fs_stat_blocked_road: "বন্ধ ৰাস্তা",
    fs_stat_avoided: "এৰাই চলা হ'ল",
    fs_stat_verified: "৯৮.৪% নিৰাপদ প্ৰমাণিত",
    fs_landmarks_title: "📍 যাত্ৰাৰ প্ৰধান স্থান আৰু জিৰণি কেন্দ্ৰ",
    
    // Radar Scanning Modal
    radar_modal_title: "নিৰাপদ বেচ কেম্প পথ বিচাৰি থকা হৈছে",
    radar_modal_sub: "অৰুণাচলৰ ভূ-প্ৰকৃতি আৰু নিম্নতম বিপদ সংকুল পথ নিৰ্ণয়",
    radar_step1_status: "এছআৰটিএম ৩০মি ডিজিটেল এলিভেচন আৰু ইনচাৰ ঢাল পৰীক্ষা...",
    radar_step1_log: "🛰️ [০১/০৫] টাৱাং খণ্ডৰ বাবে এছআৰটিএম ৩০মি উচ্চতা আৰু ইনচাৰ ঢাল পৰীক্ষা...",
    radar_step2_status: "বৰষুণ আৰু মাটিৰ আৰ্দ্ৰতা বিশ্লেষণ...",
    radar_step2_log: "🌧️ [০২/০৫] বতৰ বিজ্ঞান কেন্দ্ৰৰ লাইভ বৰষুণ আৰু মাটিৰ পানী শোষণ পৰীক্ষা...",
    radar_step3_status: "সক্ৰিয় ভূমিস্খলন আৰু বন্ধ পথ চিনাক্তকৰণ...",
    radar_step3_log: "⛔ [০৩/০৫] ভূমিস্খলন চিনাক্ত হ'ল... এনএইচ-১৩ বৈশাখী পথ সম্পূৰ্ণ বন্ধ!",
    radar_step4_status: "ডাইজকষ্ট্ৰা এলগৰিথমৰ জৰিয়তে নিৰাপদ পথ নিৰ্ণয়...",
    radar_step4_log: "🧭 [০৪/০৫] সেনা বেচ কেম্পলৈ বিপদহীন বিকল্প পথৰ সন্ধান...",
    radar_step5_status: "চেলা সুৰংগ বাইপাছৰ জৰিয়তে পথ নিশ্চিত...",
    radar_step5_log: "✅ [০৫/০৫] চেলা সুৰংগ হৈ সুৰক্ষিত পথ চিনাক্ত কৰা হ'ল (৯৮.৪% নিৰাপদ)!",

    // Offline Monitor & Gateway
    offline_monitor_title: "ভূমিস্খলন নিৰীক্ষণ ব্যৱস্থা",
    offline_mode_tag: "⚠ অফলাইন মোড",
    offline_internet_label: "ইণ্টাৰনেট:",
    offline_internet_val: "🔴 উপলব্ধ নহয়",
    offline_weather_label: "বতৰ:",
    offline_weather_val: "সংৰক্ষিত তথ্য ব্যৱহাৰ কৰা হৈছে",
    offline_last_update_label: "বতৰৰ শেষ তথ্য:",
    offline_gateway_status_disconnected: "লোৰা মেশ্ব: সংযোগ বিচ্ছিন্ন (অফলাইন)",
    offline_gateway_status_scanning: "স্থানীয় গেটৱেৰ সন্ধান কৰা হৈছে...",
    offline_gateway_status_connected: "লোৰা মেশ্ব: গেটৱেৰ সৈতে সংযুক্ত (192.168.1.1)",
    btn_search_gateway: "📡 স্থানীয় গেটৱে সংযোগ বিচাৰক",
    btn_connecting_gateway: "🔄 গেটৱেৰ সৈতে সংযোগ হৈ আছে...",
    btn_connected_gateway: "✅ স্থানীয় গেটৱেৰ সৈতে সংযুক্ত",
    offline_cached_data_title: "অফলাইন সংৰক্ষিত ভূতাত্বিক তথ্য",

    // Alerts Tab
    alerts_header: "আগতীয়া সতৰ্কবাণী বুলেটিন আৰু সুৰক্ষা",
    alert_critical_title: "জৰুৰীকালীন স্থানান্তৰ নিৰ্দেশ: টাৱাং খণ্ড",
    alert_critical_desc: "ছেঞ্চৰৰ তথ্য অনুসৰি ভূমিস্খলনৰ সম্ভাৱনা ৭৮% তকৈ অধিক। চেলা সুৰংগৰে সেনা বেচ কেম্পলৈ যাবলৈ অনুৰোধ জনোৱা হ'ল।",
    alert_siren_btn: "🚨 জৰুৰীকালীন চাইৰেন বজাওক",
    alert_ndrf_hotline: "এনডিআৰএফ হেল্পলাইন: ১০৭৮ • সেনা কন্ট্ৰোল: +৯১-৩৭৭২-২২২২২২",

    // Evacuation Tab
    evac_header: "আশ্ৰয় শিবিৰ আৰু বিছনাৰ ব্যৱস্থা",
    evac_shelter_1: "টাৱাং সেনা দুৰ্যোগ সাহায্য কেন্দ্ৰ",
    evac_shelter_2: "বমডিলা স্থানান্তৰ ষ্টেডিয়াম",
    evac_shelter_3: "অনি‍নি সামূহিক সাহায্য কেন্দ্ৰ",
    evac_call_btn: "📞 কেম্প কমাণ্ডাৰক ফোন কৰক",
    evac_nav_btn: "🧭 পথ নিৰ্দেশনা চাওক",

    // Community Report Tab
    report_header: "ৰাইজৰ দুৰ্যোগ প্ৰতিবেদন",
    report_sub: "জিলা দুৰ্যোগ ব্যৱস্থাপনা আৰু স্থানীয় গেটৱেলৈ দুৰ্যোগৰ প্ৰত্যক্ষ তথ্য প্ৰেৰণ কৰক।",
    report_type_label: "বিপদৰ প্ৰকাৰ",
    report_type_debris: "শিল বা মাটি খহি পৰা",
    report_type_crack: "মাটি ফাটি যোৱা / গাঁত হোৱা",
    report_type_subsidence: "ৰাস্তা তললৈ বহি যোৱা",
    report_location_label: "খণ্ড বা অঞ্চল",
    report_desc_label: "বিৱৰণ",
    report_desc_placeholder: "পাহাৰৰ মাটি খহি পৰা, ৰাস্তা বন্ধ হোৱা বা বোকাময় পানী দেখা পালে লিখক...",
    report_photo_btn: "📷 ফটো সংলগ্ন কৰক",
    report_submit_btn: "📤 প্ৰতিবেদন জমা দিয়ক",
    report_offline_note: "ℹ️ অফলাইন মোড: প্ৰতিবেদন সংৰক্ষণ কৰা হৈছে আৰু স্থানীয় গেটৱেৰ দ্বাৰা প্ৰেৰণ হ'ব।",

    // Weather News & Local Mobiles Alert
    weather_news_title: "স্থানীয় বতৰৰ বাতৰি আৰু বুলেটিন",
    weather_news_sub: "লাইভ আৰু অফলাইন সংৰক্ষিত বতৰ বিজ্ঞানৰ সতৰ্কবাণী (অৰুণাচল খণ্ড)",
    filter_all_sectors: "সকলো খণ্ড",
    filter_tawang: "টাৱাং",
    filter_bomdila: "বমডিলা",
    filter_anini: "অনি‍নি",
    filter_pasighat: "পাছিঘাট",
    filter_itanagar: "ইটানগৰ",
    offline_cached_badge: "💾 অফলাইন সংৰক্ষিত (ইণ্টাৰনেট নোহোৱাকৈ উপলব্ধ)",
    live_synced_badge: "🟢 লাইভ আইএমডি আৰু উপগ্ৰহ সংযোগ সক্ৰিয়",
    btn_sync_weather: "🔄 বতৰৰ বুলেটিন সতেজ কৰক",
    btn_send_local_mobiles: "স্থানীয় মোবাইললৈ বাৰ্তা প্ৰেৰণ কৰক",
    local_mobiles_modal_title: "স্থানীয় চেল ব্ৰডকাষ্ট আৰু লোৰা বাৰ্তা প্ৰেৰণ",
    local_mobiles_modal_sub: "খণ্ডৰ সকলো নাগৰিকৰ ফোনলৈ জৰুৰী সতৰ্কবাণী প্ৰেৰণ কৰা হৈছে",
    mobiles_alerted_label: "সতৰ্ক কৰা মোবাইল",
    broadcast_protocol_label: "প্ৰট'কল",
    delivery_status_label: "প্ৰেৰণৰ স্থিতি",
    btn_dismiss_alert: "বন্ধ কৰক",

    // Comparative Risk Bar Chart & 48h Local Weather Prediction
    risk_bar_chart_title: "তুলনামূলক খণ্ড ভূমিস্খলন বিপদ প্ৰ'ফাইল",
    risk_bar_chart_sub: "অৰুণাচলৰ পাহাৰীয়া খণ্ডসমূহত ৰিয়েল-টাইম এআই বিপদ মূল্যায়ন",
    risk_by_place_title: "📊 স্থান অনুসৰি বিপদাশংকা",
    risk_by_place_sub: "অৰুণাচলৰ প্ৰধান স্থানসমূহত ৰিয়েল-টাইম এআই বিপদ সম্ভাৱনা",
    slope_vs_risk_title: "📈 ঢাল বনাম বিপদাশংকা",
    slope_vs_risk_sub: "পাহাৰৰ ঢালৰ নতি বনাম ভূমিস্খলনৰ সম্ভাৱনা",
    critical_threshold_label: "৭০% গুৰুতৰ বিপদৰ সীমা (স্থানান্তৰ ট্ৰিগাৰ)",
    selected_sector_badge: "📍 নিৰ্বাচিত খণ্ড",
    weather_prediction_title: "স্থানীয় বতৰ কাৰ্যালয়ৰ ৪৮ ঘণ্টাৰ বতৰৰ পূৰ্বানুমান",
    weather_office_source: "📡 অফিচিয়েল টেলিমেট্ৰি: আঞ্চলিক বতৰ বিজ্ঞান কেন্দ্ৰ (আৰএমচি ইটানগৰ / আইএমডি)",
    past_24h_label: "বিগত ২৪ ঘণ্টা (ৰেকৰ্ড বৰষুণ)",
    next_24h_label: "পৰৱৰ্তী ২৪ ঘণ্টা (পূৰ্বানুমান)",
    peak_hazard_window: "ডাৱৰ বিস্ফোৰণৰ চৰম বিপদৰ সময়",
    weather_station_label: "স্থানীয় বতৰ কেন্দ্ৰ",
    now_marker_label: "এতিয়া (লাইভ)",
    cloudburst_forecast_note: "⚠️ +৬ৰ পৰা +১২ ঘণ্টাৰ ভিতৰত প্ৰচণ্ড ডাৱৰ বিস্ফোৰণৰ সতৰ্কবাণী। মাটিৰ জলপৃষ্ঠ অতিমাত্ৰা বৃদ্ধি পাব পাৰে।",

    // Evacuation Tab Polished Header
    evac_hero_badge: "👨‍👩‍👧 অৰুণাচল সুৰক্ষা প্ৰট'কল",
    evac_gnn_badge: "⚡ জিএনএন এআই ইঞ্জিন সক্ৰিয়",
    evac_main_title: "ক্ষুদ্ৰ-স্থানান্তৰ অগ্ৰাধিকাৰ তালিকা",
    evac_main_desc: "পাহাৰীয়া ঢালৰ নিকটৱৰ্তিতা, শিশু/বৃদ্ধৰ সংখ্যা আৰু ভূমিস্খলনৰ আশংকাৰ ভিত্তিত টাৱাং, অনি‍নি আৰু বمডিলাৰ স্পৰ্শকাতৰ পৰিয়ালসমূহৰ জিএনএন ৰেংকিং।",
    evac_stat_homes: "৫ টা অগ্ৰাধিকাৰ পৰিয়াল",
    evac_stat_homes_lbl: "জৰুৰী স্থানান্তৰ",
    evac_stat_shelters: "১০০% আবণ্টিত",
    evac_stat_shelters_lbl: "সুৰক্ষিত আশ্ৰয় শিবিৰ",
    evac_stat_window: "১৫ মিনিটৰ সময়",
    evac_stat_window_lbl: "সুৰক্ষিত যাত্ৰাৰ সীমা",

    // Telemetry Tab - Official Situation PDF & Government / District Collector Dispatch
    gov_dispatch_badge: "🤖 স্বায়ত্তশাসিত এআই প্ৰহৰী",
    gov_dispatch_title: "জিলা উপায়ুক্তলৈ এআই দুৰ্যোগ ভৱিষ্যদ্বাণী ডচিয়াৰ",
    gov_dispatch_sub: "প্ৰশাসনিক হস্তক্ষেপৰ বাবে স্বায়ত্তশাসিত এজ-এআইয়ে টেলিমিত্ৰি, ৪৮ ঘণ্টাৰ ডাৱৰ বিস্ফোৰণ মডেল আৰু নিৰ্দেশনাসমূহ বিশ্লেষণ কৰে।",
    btn_gov_dispatch: "⚡ জিলা উপায়ুক্তলৈ এআই ভৱিষ্যদ্বাণী ডচিয়াৰ প্ৰেৰণ কৰক",
    dispatch_step1: "🧠 ফিজিক্স-ইনফৰ্মড জিএনএন v3.4 ৰ দ্বাৰা সংবেদক আৰু উপগ্ৰহ ৰাডাৰ বিশ্লেষণ চলিছে...",
    dispatch_step2: "🔮 ডিপ লাৰ্নিং ইঞ্জিনৰ দ্বাৰা ৪৮ ঘণ্টাৰ ডাৱৰ বিস্ফোৰণ আৰু বিপদাশংকা গণনা কৰা হৈছে...",
    dispatch_step3: "📡 এআই ডচিয়াৰ এনক্ৰিপ্ট কৰি লোৰা মেশ্বযোগে উপায়ুক্ত আৰু ডিইঅ'চিক প্ৰেৰণ কৰা হৈছে...",
    dispatch_step4: "✅ এআই ভৱিষ্যদ্বাণী ডচিয়াৰ জিলা উপায়ুক্ত আৰু প্ৰশাসনক সফলভাৱে বিতৰণ কৰা হ'ল!",
    pdf_modal_title: "এআই আগতীয়া সতৰ্কবাণী দুৰ্যোগ ভৱিষ্যদ্বাণী ডচিয়াৰ",
    pdf_modal_sub: "এনইআৰ কৱচ স্বায়ত্ত এআই প্ৰহৰী • জিলা উপায়ুক্ত আৰু প্ৰশাসনলৈ প্ৰেৰিত",
    btn_download_pdf: "📥 এআই ডচিয়াৰ ডাউনলোড কৰক",
    btn_print_pdf: "🖨️ প্ৰিণ্ট / পিডিএফ সংৰক্ষণ কৰক",
    btn_close_pdf: "✖ বন্ধ কৰক"
  },

  bn: {
    // App Header & Modes
    app_title: "এনইআর কবচ",
    app_subtitle: "এআই পূর্বাভাস ও প্রাথমিক সতর্কতা",
    online_auto: "অনলাইন অটো",
    offline_auto: "অফলাইন অটো",
    news_tab: "📰 সংবাদ",
    status_online: "অনলাইন",
    status_offline: "অফলাইন",
    lang_btn_label: "বাংলা",
    lang_modal_title: "ভাষা নির্বাচন করুন",
    lang_modal_subtitle: "ভূমিধসের প্রাথমিক সতর্কতা এবং দুর্যোগ ব্যবস্থাপনার জন্য আপনার পছন্দের ভাষা বেছে নিন",
    lang_continue_btn: "অ্যাপে এগিয়ে যান",

    // Top 4 KPI Cards
    kpi_high_risk_title: "উচ্চ ঝুঁকিপূর্ণ এলাকা",
    kpi_high_risk_sub: "তাওয়াং • বমডিলা • অনীনি ℹ️",
    kpi_roads_title: "মহাসড়ক অবরোধ",
    kpi_roads_sub: "এনএইচ-১৩ (রূপা) • এনএইচ-৩১৩ ℹ️",
    kpi_shelters_title: "সেনা ও ত্রাণ শিবির",
    kpi_shelters_sub: "৪,৯০০ বেডের সুবিধা ℹ️",
    kpi_mesh_title: "অরুণাচল গেটওয়ে",
    kpi_mesh_sub: "৫/৫ নোড সক্রিয় (868MHz) ℹ️",
    active_tag: "সক্রিয়",
    blocked_tag: "অবরুদ্ধ",
    critical_tag: "জরুরি",
    lora_mesh_tag: "লোরা মেশ",

    // Bottom Navigation Bar
    nav_ai_twin: "এআই টুইন",
    nav_telemetry: "টেলিমেট্রি",
    nav_alerts: "সতর্কতা",
    nav_evacuation: "স্থানান্তর",
    nav_report: "রিপোর্ট",

    // AI Twin Tab
    sector_select_title: "অরুণাচল সেক্টর নির্বাচন",
    sector_districts: "৫টি জেলা",
    xgboost_twin_title: "জিওটেকনিক্যাল এক্সজিবুস্ট এআই ডিজিটাল টুইন",
    xgboost_twin_sub: "পূর্ব হিমালয়ের ভূতাত্ত্বিক বৈশিষ্ট্য অনুযায়ী ক্যালিব্রেট করা",
    hazard_high: "উচ্চ ঝুঁকি (বিপজ্জনক)",
    hazard_moderate: "মাঝারি ঝুঁকি",
    hazard_low: "কম ঝুঁকি (নিরাপদ)",
    confidence_band: "নির্ভরযোগ্যতার মাত্রা: ৮৭% ± ৫%",
    rainfall_slider_label: "২৪ ঘণ্টার মোট বৃষ্টিপাত",
    soil_moisture_slider_label: "মাটির আর্দ্রতার পরিমাণ",
    slope_gradient_slider_label: "পাহাড়ের ঢালের খাড়া ভাব",
    insar_velocity_slider_label: "ইনসার ভূ-গতির বেগ",

    // Telemetry Tab
    telemetry_section_title: "সেক্টর টেলিমেট্রি ও সোলার নোড",
    solar_batt_label: "সোলার ব্যাটারি",
    stat_24h_rainfall: "২৪ ঘণ্টার বৃষ্টি",
    stat_soil_moisture: "মাটির আর্দ্রতা",
    stat_slope_gradient: "ঢালের কোণ",
    stat_insar_velocity: "ইনসার গতি",
    highway_status_label: "এনএইচ-১৩",
    highway_blocked_val: "অবরুদ্ধ (বৈশাখী)",
    shelter_status_label: "ত্রাণ শিবির",
    shelter_status_val: "তাওয়াং সেনা দুর্যোগ কেন্দ্র (৮৫০/১২০০ বেড)",
    
    // Evacuation Route & Radar Operations
    safe_evac_card_title: "সেনা বেস ক্যাম্পে নিরাপদ স্থানান্তর",
    safe_evac_card_desc: "আপনার সেক্টর থেকে সুরক্ষিত সেনা বেস ক্যাম্প পর্যন্ত সবচেয়ে নিরাপদ স্থানান্তর পথ নির্ণয় করে।",
    btn_find_route: "🧭 নিরাপদ বেস ক্যাম্প পথ খুঁজুন",
    btn_view_fullscreen: "🧭 পূর্ণ স্ক্রিন মানচিত্র দেখুন",
    btn_computing_route: "⏳ নিরাপদ পথ খোঁজা হচ্ছে...",
    btn_recalculate_route: "🔄 পথ পুনরায় নির্ণয় করুন",
    return_to_telemetry: "← টেলিমেট্রিতে ফিরে যান",
    fs_route_title: "সবচেয়ে নিরাপদ স্থানান্তর রুট",
    fs_stat_distance: "মোট দূরত্ব",
    fs_stat_travel_time: "যাত্রার সময়",
    fs_stat_safety_index: "সুরক্ষা সূচক",
    fs_stat_blocked_road: "অবরুদ্ধ রাস্তা",
    fs_stat_avoided: "এড়িয়ে চলা হয়েছে",
    fs_stat_verified: "৯৮.৪% নিরাপদ প্রমাণিত",
    fs_landmarks_title: "📍 প্রধান ভৌগোলিক ল্যান্ডমার্ক ও স্থানান্তর কেন্দ্র",
    
    // Radar Scanning Modal
    radar_modal_title: "নিরাপদ বেস ক্যাম্প রুট খোঁজা হচ্ছে",
    radar_modal_sub: "অরুণাচলের ভূ-তত্ত্ব ও ঝুঁকিহীন করিডোর পাথফাইন্ডিং",
    radar_step1_status: "এসআরটিএম ৩০মি ডিজিটাল এলিভেশন মডেল ও ইনসার ঢাল যাচাই...",
    radar_step1_log: "🛰️ [০১/০৫] তাওয়াং সেক্টরের এসআরটিএম ৩০মি ও ইনসার ঢাল যাচাই করা হচ্ছে...",
    radar_step2_status: "বৃষ্টিপাত ও মাটির আর্দ্রতা বিশ্লেষণ...",
    radar_step2_log: "🌧️ [০২/০৫] রিয়েল-টাইম বৃষ্টিপাত ও মাটির জলীয় সম্পৃক্ততা পর্যবেক্ষণ...",
    radar_step3_status: "সক্রিয় ভূমিধস ও অবরুদ্ধ পথ শনাক্তকরণ...",
    radar_step3_log: "⛔ [০৩/০৫] ভূমিধসের ধ্বংসাবশেষ চিহ্নিত... এনএইচ-১৩ বৈশাখী রাস্তা অবরুদ্ধ!",
    radar_step4_status: "ডাইকস্ট্রা অ্যালগরিদমের মাধ্যমে নিরাপদ রুট নির্ধারণ...",
    radar_step4_log: "🧭 [০৪/০৫] সেনা বেস ক্যাম্পের জন্য বিকল্প ঝুঁকিমুক্ত পথ নির্ণয়...",
    radar_step5_status: "সেলা টানেল বাইপাসের মাধ্যমে রুট নিশ্চিত...",
    radar_step5_log: "✅ [০৫/০৫] সেলা টানেল বাইপাস দিয়ে নিরাপদ রুট নিশ্চিত (৯৮.৪% সুরক্ষা সূচক)!",

    // Offline Monitor & Gateway
    offline_monitor_title: "ভূমিধস পর্যবেক্ষণ ব্যবস্থা",
    offline_mode_tag: "⚠ অফলাইন মোড",
    offline_internet_label: "ইন্টারনেট:",
    offline_internet_val: "🔴 উপলব্ধ নেই",
    offline_weather_label: "আবহাওয়া:",
    offline_weather_val: "ক্যাশ করা পূর্বাভাস ব্যবহৃত",
    offline_last_update_label: "শেষ আবহাওয়া আপডেট:",
    offline_gateway_status_disconnected: "লোরা মেশ: সংযোগ বিচ্ছিন্ন (অফলাইন)",
    offline_gateway_status_scanning: "স্থানীয় গেটওয়ের অনুসন্ধান চলছে...",
    offline_gateway_status_connected: "লোরা মেশ: গেটওয়ের সাথে যুক্ত (192.168.1.1)",
    btn_search_gateway: "📡 স্থানীয় গেটওয়ে অনুসন্ধান করুন",
    btn_connecting_gateway: "🔄 গেটওয়ের সাথে যুক্ত হচ্ছে...",
    btn_connected_gateway: "✅ স্থানীয় গেটওয়ের সাথে যুক্ত",
    offline_cached_data_title: "অফলাইনে সংরক্ষিত ভূতাত্ত্বিক ঝুঁকি",

    // Alerts Tab
    alerts_header: "আগাম সতর্কবার্তা ও নাগরিক সুরক্ষা",
    alert_critical_title: "জরুরি স্থানান্তর নির্দেশ: তাওয়াং সেক্টর",
    alert_critical_desc: "সেন্সরের তথ্য অনুযায়ী ভূমিধসের আশঙ্কা ৭৮% এর বেশি। সেলা টানেল বাইপাস দিয়ে সেনা ক্যাম্পে যাওয়ার পরামর্শ দেওয়া হচ্ছে।",
    alert_siren_btn: "🚨 জরুরি সাইরেন বাজান",
    alert_ndrf_hotline: "এনডিআরএফ হেল্পলাইন: ১০৭৮ • সেনা কন্ট্রোল: +৯১-৩৭৭২-২২২২২২",

    // Evacuation Tab
    evac_header: "ত্রাণ শিবির ও বেড সংখ্যা",
    evac_shelter_1: "তাওয়াং সেনা দুর্যোগ ত্রাণ কেন্দ্র",
    evac_shelter_2: "বমডিলা সিভিল স্থানান্তর স্টেডিয়াম",
    evac_shelter_3: "অনীনি কমিউনিটি জরুরি আশ্রয়স্থল",
    evac_call_btn: "📞 ক্যাম্প কমান্ডারের সাথে যোগাযোগ",
    evac_nav_btn: "🧭 শিবিরের পথ দেখুন",

    // Community Report Tab
    report_header: "কমিউনিটি দুর্যোগ রিপোর্ট",
    report_sub: "জেলা দুর্যোগ প্রতিক্রিয়া কেন্দ্র (DEOC) এবং স্থানীয় গেটওয়ে মেশে ভূমির প্রত্যক্ষ তথ্য পাঠান।",
    report_type_label: "ঝুঁকির ধরন",
    report_type_debris: "সক্রিয় ভূমিধস বা পাথর খসে পড়া",
    report_type_crack: "মাটিতে ফাটল সৃষ্টি হওয়া",
    report_type_subsidence: "রাস্তা দেবে যাওয়া",
    report_location_label: "সেক্টর বা অবস্থান",
    report_desc_label: "বিবরণ ও বিবরণী",
    report_desc_placeholder: "পাহাড় থেকে পাথর খসা, রাস্তা বন্ধ বা কাদা জলের প্রবাহ লিখুন...",
    report_photo_btn: "📷 ঘটনার ছবি যুক্ত করুন",
    report_submit_btn: "📤 রিপোর্ট জমা দিন",
    report_offline_note: "ℹ️ অফলাইন মোড: রিপোর্ট সংরক্ষিত হয়েছে এবং লোকাল গেটওয়ে লোরা মেশের মাধ্যমে পাঠানো হবে।",

    // Weather News & Local Mobiles Alert
    weather_news_title: "স্থানীয় আবহাওয়া সংবাদ ও বুলেটিন",
    weather_news_sub: "লাইভ ও অফলাইন সংরক্ষিত আবহাওয়া সতর্কতা (অরুণাচল করিডোর)",
    filter_all_sectors: "সকল সেক্টর",
    filter_tawang: "তাওয়াং",
    filter_bomdila: "বমডিলা",
    filter_anini: "অনীনি",
    filter_pasighat: "পাসিঘাট",
    filter_itanagar: "ইটানগর",
    offline_cached_badge: "💾 অফলাইনে সংরক্ষিত (ইন্টারনেট ছাড়াও উপলব্ধ)",
    live_synced_badge: "🟢 লাইভ আইএমডি ও স্যাটেলাইট গ্রিড সংযুক্ত",
    btn_sync_weather: "🔄 আবহাওয়া বুলেটিন রিফ্রেশ করুন",
    btn_send_local_mobiles: "স্থানীয় মোবাইলে বার্তা পাঠান",
    local_mobiles_modal_title: "স্থানীয় সেল ব্রডকাস্ট ও লোরা এসএমএস প্রেরণ",
    local_mobiles_modal_sub: "সেক্টরের সকল নাগরিক মোবাইলে সরাসরি জরুরি সতর্কতা পাঠানো হয়েছে",
    mobiles_alerted_label: "সতর্কিত মোবাইল",
    broadcast_protocol_label: "প্রোটোকল",
    delivery_status_label: "ডেলিভারি স্থিতি",
    btn_dismiss_alert: "রসিদ বন্ধ করুন",

    // Comparative Risk Bar Chart & 48h Local Weather Prediction
    risk_bar_chart_title: "তুলনামূলক সেক্টর ভূমিধস ঝুঁকি প্রোফাইল",
    risk_bar_chart_sub: "অরুণাচল করিডোরের বিভিন্ন সেক্টরে রিয়েল-টাইম এআই ঝুঁকি স্কোরিং",
    risk_by_place_title: "📊 স্থান অনুযায়ী ঝুঁকি",
    risk_by_place_sub: "অরুণাচলের প্রধান স্থানসমূহে রিয়েল-টাইম এআই ঝুঁকি সম্ভাবনা",
    slope_vs_risk_title: "📈 ঢাল বনাম ঝুঁকি",
    slope_vs_risk_sub: "পাহাড়ের ঢালের নতি বনাম ভূমিধসের সম্ভাবনা",
    critical_threshold_label: "৭০% গুরুতর ঝুঁকি সীমা (স্থানান্তর ট্রিগার)",
    selected_sector_badge: "📍 নির্বাচিত সেক্টর",
    weather_prediction_title: "স্থানীয় আবহাওয়া অফিসের ৪৮ ঘণ্টার পূর্বাভাস",
    weather_office_source: "📡 অফিশিয়াল টেলিমেট্রি: আঞ্চলিক আবহাওয়া কেন্দ্র (আরএমসি ইটানগর / আইএমডি)",
    past_24h_label: "গত ২৪ ঘণ্টা (রেকর্ড বৃষ্টিপাত)",
    next_24h_label: "পরবর্তী ২৪ ঘণ্টা (পূর্বাভাস)",
    peak_hazard_window: "মেঘভাঙা বৃষ্টির চরম ঝুঁকির সময়",
    weather_station_label: "স্থানীয় আবহাওয়া স্টেশন",
    now_marker_label: "এখন (লাইভ)",
    cloudburst_forecast_note: "⚠️ +৬ থেকে +১২ ঘণ্টার মধ্যে তীব্র মেঘভাঙা বৃষ্টির সতর্কতা। মাটির আর্দ্রতা বিপদসীমা অতিক্রম করতে পারে।",

    // Evacuation Tab Polished Header
    evac_hero_badge: "👨‍👩‍👧 অরুণাচল সুরক্ষা প্রটোকল",
    evac_gnn_badge: "⚡ জিএনএন এআই ইঞ্জিন সক্রিয়",
    evac_main_title: "মাইক্রো-উদ্বাস্তুকরণ অগ্রাধিকার তালিকা",
    evac_main_desc: "পাহাড়ের ঢালের নৈকট্য, শিশু/বৃদ্ধের অনুপাত এবং ভূমিধসের ঝুঁকির ভিত্তিতে তাওয়াং, অনীনি এবং বমডিলা করিডোরের দুর্বল পরিবারের জিএনএন র্যাঙ্কিং।",
    evac_stat_homes: "৫টি অগ্রাধিকার গৃহ",
    evac_stat_homes_lbl: "জরুরি স্থানান্তর",
    evac_stat_shelters: "১০০% নির্ধারিত",
    evac_stat_shelters_lbl: "সুরক্ষিত আশ্রয় শিবির",
    evac_stat_window: "১৫ মিনিট সময়",
    evac_stat_window_lbl: "নিরাপদ যাতায়াত",

    // Telemetry Tab - Official Situation PDF & Government / District Collector Dispatch
    gov_dispatch_badge: "🤖 স্বায়ত্তশাসিত এআই প্রহরী",
    gov_dispatch_title: "জেলা কালেক্টরের জন্য এআই দুর্যোগ পূর্বাভাস ডজিয়ার",
    gov_dispatch_sub: "প্রশাসনিক হস্তক্ষেপের জন্য স্বায়ত্তশাসিত এজ-এআই টেলিমেট্রি, ৪৮ ঘণ্টার মেঘভাঙা বৃষ্টির মডেল এবং সতর্কতামূলক নির্দেশাবলী সংশ্লেষ করে।",
    btn_gov_dispatch: "⚡ জেলা কালেক্টরকে এআই পূর্বাভাস ডজিয়ার পাঠান",
    dispatch_step1: "🧠 ফিজিক্স-ইনফর্মড জিএনএন v3.4 রিয়েল-টাইম সেন্সর স্ট্রিম ও উপগ্রহ রাডার বিশ্লেষণ করছে...",
    dispatch_step2: "🔮 ডিপ লার্নিং ইঞ্জিন ৪৮ ঘণ্টার মেঘভাঙা বৃষ্টি ও ধসের সম্ভাব্যতা নিরূপণ করছে...",
    dispatch_step3: "📡 এআই ডজিয়ার এনক্রিপ্ট করে লোরা মেশের মাধ্যমে জেলা কালেক্টর ও ডিইওসিতে পাঠানো হচ্ছে...",
    dispatch_step4: "✅ এআই পূর্বাভাস ডজিয়ার জেলা কালেক্টর ও স্থানীয় প্রশাসনকে সফলভাবে পাঠানো হয়েছে!",
    pdf_modal_title: "এআই প্রাথমিক সতর্কতা দুর্যোগ পূর্বাভাস ডজিয়ার",
    pdf_modal_sub: "এনইআর কবচ স্বায়ত্তশাসিত এআই প্রহরী • জেলা কালেক্টর ও প্রশাসনের জন্য প্রেরিত",
    btn_download_pdf: "📥 এআই ডজিয়ার ডাউনলোড করুন",
    btn_print_pdf: "🖨️ প্রিন্ট / পিডিএফ সংরক্ষণ করুন",
    btn_close_pdf: "✖ বন্ধ করুন"
  }
};

// Global active language helper
window.I18N = {
  current: 'en',
  selectedTemp: 'en',

  init: function() {
    const saved = localStorage.getItem('ner_kavach_language');
    if (saved && I18N_TRANSLATIONS[saved]) {
      this.current = saved;
      this.selectedTemp = saved;
    } else {
      this.current = 'en';
      this.selectedTemp = 'en';
    }
    this.apply(this.current);
  },

  t: function(key, fallback = '') {
    const langDict = I18N_TRANSLATIONS[this.current] || I18N_TRANSLATIONS.en;
    if (langDict && langDict[key]) return langDict[key];
    const enDict = I18N_TRANSLATIONS.en;
    if (enDict && enDict[key]) return enDict[key];
    return fallback || key;
  },

  setLanguage: function(code) {
    if (!I18N_TRANSLATIONS[code]) code = 'en';
    this.current = code;
    this.selectedTemp = code;
    localStorage.setItem('ner_kavach_language', code);
    this.apply(code);
  },

  apply: function(code) {
    document.documentElement.lang = code;
    
    // Update all elements with data-i18n
    const elements = document.querySelectorAll('[data-i18n]');
    elements.forEach(el => {
      const key = el.getAttribute('data-i18n');
      const translation = this.t(key);
      if (translation) {
        if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA') {
          el.placeholder = translation;
        } else {
          el.innerText = translation;
        }
      }
    });

    // Update placeholders with data-i18n-placeholder
    const placeholders = document.querySelectorAll('[data-i18n-placeholder]');
    placeholders.forEach(el => {
      const key = el.getAttribute('data-i18n-placeholder');
      const translation = this.t(key);
      if (translation) {
        el.setAttribute('placeholder', translation);
      }
    });

    // Update Header Language Button label
    const currentLangObj = I18N_LANGUAGES.find(l => l.code === code) || I18N_LANGUAGES[0];
    const langCodeEl = document.getElementById('current-lang-code');
    if (langCodeEl) {
      langCodeEl.innerText = currentLangObj.native;
    }

    // Refresh dynamic cards if functions exist
    if (window.renderDynamicTelemetryCard) {
      window.renderDynamicTelemetryCard();
    }
  }
};
