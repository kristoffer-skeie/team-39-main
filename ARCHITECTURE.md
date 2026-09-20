# Arkitekturen i prosjektet:
Vi bruker MVVM-modellen i appen vår. Ved at vi har skjermer som alle har sin viewmodel, og viewmodel henter data fra repository som ligger i datalaget. Dette gjør at appen kjører optimalt. Vi har valgt at appen kan kjøre på API-level 34 android telefoner. Dette fordi at prosjektet kan kjøre på alle android telefoner siden 34 er den nyeste versjonen. Vi har valgt å bruke google maps for å vise kart. Google maps krever høyt API level og det oppdaterer seg jevnlig, derfor valgte vi det høyeste mulige API levelet. TargetSdk will need to be 34+ for new apps and app updates by August 31, 2024. Med dette i betraktning ville vi vurdert å endre til MapBox dersom vi skulle vedlikeholdt appen. Alt i alt har de fleste filene i prosjektet relativt lav kobling og høy kohesjon. Dette gjør at vi unngår en del teknisk gjeld.

I denne filen har vi valgt å dele opp prosjektet i View og ViewModel som sammen danner UI, og Repository og DataSource som danner Model

## UI (View og ViewModel)
###### UI-laget inneholder skjermer og viewmodel. Siden vi følger MVVM-prinsipper, kan vi se på Screen som View-delen av MVVM.. Navigasjon brukes for å navigere mellom de ulike skjermene og alle skjermene har en viewmodel. De har også en ekstra maincontent-fil som skal hjelpe med å endre utseendet på sidene. Skjermer representerer data på en intuitiv måte og henter denne dataen fra data-laget som forklares senere i teksten. Denne sammenhengen mellom skjermer, viewmodel og repository utgjør MVVM-modellen. 

#### Navigation:
  Holder styr på navigasjonen mellom de ulike sidene. Den har høy kohesjon fordi den har et veldig tydelig og enkelt ansvar, å navigere mellom skjermer. Den oppnår også dette kun med én enkelt funksjon. Den har lav kobling fordi den ikke er avhengig av noen andre filer enn screens. Den er til kun for å navigere mellom de ulike screensene. Den oppretter ruter med navn som refererer til kall på skjermene. Så så lenge screen-filene faktisk eksisterer vil denne kjøre uten problemer.

#### Home:
  HomeScreen viser hjemskjermen. Hjemskjermen fungerer kun som en slags startside når appen starter i tillegg til å navigere til settings og map. Utover dette har HomeScreen lite funksjonalitet. Grunnet dette har den høy kohesjon fordi den har et lite og definert ansvarsområde. Den har lav kobling fordi den ikke er avhengig av noe annet enn settings og map. Litt samme grunnlag som Navigation beskrevet over. 

#### Map:
  MapScreen, MapViewModel og MapMainContent utgjør kartsiden på appen. Ved hjelp av google maps sitt API lager vi et kart. Her kan vi hente longitude og latitude til API slik at bruker ikke trenger å taste det inn manuelt (selvom dette også er mulig). MapScreen og MapViewModel er kun til for å finne ønsket posisjon. Appen vil derfor kunne tjene samme funksjonalitet selvom map er borte, fordi bruker fortsatt kan taste inn input selv. Appen trenger bare posisjon for å kunne hente fra API. MapScreen er fullstendig avhengig av MapViewModel og har derfor noe høyere kobling enn f.eks. Home og Navigation vi har sett på ovenfor. Den har også høy kohesjon fordi den bare viser en kartskjerm og inneholder et inputfelt for latitude, longitude og altitude. Det er slik det bør være siden vi følger MVVM-modellen for arkitekturen, slik at Screens og ViewModels kun står for businesslogikk og bare skal vise frem dataen som hentes fra datalaget. MapViewModel kontrollerer endring av input fra kartet. Den har et bestemt ansvar og derfor høy kohesjon, i tillegg har den få avhengigheter og derfor lav kobling.
  
#### Favorite:
  FavoriteScreen og FavoriteViewModel viser tidspunktet for et områder som bruker har valgt å favorisere. Dette lagres slik at brukeren kan enkelt sjekke et bestemt område for et bestemt tidspunkt. FavoriteScreen fungerer relativt likt de andre skjermene i prosjektet. Den har derfor høy kohesjon fordi den har lite ansvar(kun vise favoritter) og lav kobling fordi den bare er avhengig av favorittene den får fra FavoriteViewModel. FavoriteViewMode har litt lavere kohesjon enn FavoriteScreen fordi den har funksjoner for å f.eks. loade, adde og remove favoritter, som kaller på Repository sine funksjoner. Derfor er den avhengig av at den faktisk får hentet favoritter fra FavoriteRepository.
  
#### Contact:
  ContactScreen og ContactViewModel har lite avhengigheter og har kun som oppgave å sende en mail til luftverntilsynet, som er en uavhengig tredjepart. Disse filene har kanskje de mest spesifikke rollene i hele appen. I tillegg til å være minst avhengige. 
  
#### Settings:
  SettingsScreen og SettingsViewModel gir mulighet for å endre preferanser på appen. Vi har bare en funksjonalitet på settings som gir mulighet for å endre utseende på appen mellom mørk og lys modus. Selvom dette er en spesifikk funksjon, kan settings endre seg og få mye ulik funksjonalitet og har derfor mulighet for et bredere ansvar. Dette gjør at den har mulighet for å få noe lavere kohesjon.

#### WeatherForecast:
  Benytter MVVM-mønsteret, der WeatherForecastViewModel fungerer som mellomleddet mellom grensesnittet (View) og datakildene(Model og Data). WeatherForecastViewModel inneholder logikk knyttet til presentasjon og behandling av værdata. Den henter data fra både WeatherForecast- og GRIB2Repository. Den holder på UIState og inneholder svært mange funksjoner som vi kan se på klassediagrammet i MODELING.md. Grunnet dette har den lavere kohesjon enn resten av ViewModel-filene i appen. Den har også høyere kobling fordi den er avhengig av både WeatherDataRepository og GRIB2Repository. WeatherForecastScreen Representerer utseendet på appen og viser kartet og en liste med værforhold de ulike dagene. Disse filene har høy kohesjon fordi de kun håndterer logikk som er spesifikk for visning av dataen. De har lav kobling fordi de kun fokuserer på presentasjon av dataen.

## Data (Model)
###### Data-laget inneholder repository, datasource og en database. Det er her selve logikken og håndteringen av rå data foregår. DataSource-filene henter data fra API og gjør den lett håndterbar og sender til repository. Repository inneholder en UiState og funksjoner for å håndtere og endre dataen den får fra DataSource. For å lagre favoritter har vi også opprettet en database for å ta vare på tidspunktene. Repository sender også data til ViewModel som igjen sender til Screen(view) slik at dataen kan bli vist på en intuitiv og lesbar måte. Sammen med UI-laget følger vi på denne måten MVVM-modellen.

#### WeatherForecast:
  WeatherForecastDataSource henter værdata fra MET sitt weatherforecast-API. WeatherForecastRepository er ansvarlig for behandling av værdataen den får fra DataSource. Den har lav kohesjon fordi den har ansvar for mange oppgaver innen ulike funksjonelle områder i appen og har høy kobling fordi den er avhengig av dataen den får fra DataSource
  
#### GRIB2:
  Filene GRIB2DataSource og GRIB2Repository har mange likheter med weatherForecast, ved at de også henter API fra MET. GRIB2Repository gir også data til WeatherForecastViewModel fordi GRIB-dataen vises sammen med WeatherForecastDataen.
  
#### Favorite:
  FavoriteForecastRepository inneholder logikk for henting, fjerning og tillegging av favoritter. FavoriteViewModel kaller disse funksjonene for å vise frem alle favoritter som hentes fra FavoriteDatabase. FavoriteForecastRepository har et tydelig ansvarsområde og er avhengig av at databasen fungerer som den skal. Derfor har den høy kohesjon og lav kobling. 
