### Funksjonelle krav:
 -Vi skal lage en oppsummeringsside der bruker får en rask tilbakemelding på om det er ønskelig og lovlig å sende opp en rakett.\
 -Vi skal lage funksjonalitet for å se mer detaljert om vær på posisjonen.\
 -Vi skal lage funksjonalitet for å endre og justere størrelsen på raketten for mer detaljerte tilbakemeldinger ved de andre kravene.\
 -Lage funksjonalitet for å kontakte luftverns-tilsynet.
 
### Use Case:

![Use Case](https://github.uio.no/IN2000-V24/team-39/blob/main/useCase.png?raw=true)

#### 1. Vi skal lage en oppsummeringsside der bruker får en rask tilbakemelding på om det er ønskelig og lovlig å sende opp en rakett:
##### Tekstlig beskrivelse av Use Case - _"Rask tilbakemelding":_

 Aktører:\
 -Bruker\
 -Meteorologisk institutt - API
 
 Prebetingelser:\
 -Bruker er pålogget applikasjonen.\
 -Området har data om værforhold\
 -Bruker har et ønsket område for oppskytning
 
 Postbetingelser:\
 -Presentere oppskytningstider til den eksterne brukeren for å gi rask tilbakemelding.
 
 Hovedflyt:
 1. Bruker velger ønsket område for oppskytning (ved longitude og latitude eller på kart).
 2. Systemet gir kort oppsummering av været for de neste dagene.
 3. Bruker kan trykke på dager og enkelt se oppsummert farge for hver tid
 
 Alternativflyt:\
 2.1 Systemet mangler data.\
 2.2 Bruker blir bedt om å velge et nytt området.
 
#### 2. Vi skal lage funksjonalitet for å se mer detaljert om vær på posisjonen.
##### Tekstlig beskrivelse av Use Case - _"Område valgt på kart":_

 Aktører:\
 -Bruker\
 -Meteorologisk institutt - API
 
 Prebetingelser:\
 -Bruker er pålogget applikasjonen.\
 -Søknaden om rakettoppskytning er opprettet av brukeren.
 
 Postbetingelser:\
 -Velge område på et kart for så å vise detaljert informasjon om værforholdene i det valgte området, samt vise om området er lovlig
 
 Hovedflyt:
 1. Bruker logger inn på applikasjonen.
 2. Bruker velger ønsket område for oppskytning.
 3. Systemet henter data for hver dag den neste uken
 4. Bruker velger dag
 5. Systemet viser farge og minimal data for hver time den valgte dagen
 6. Bruker velger en time
 7. Systemet genererer en side med med fargekode for valgt område på bestemt dato og tid:
 8. For hver tid vises detaltjert info om hvert kriterie for godkjent oppskytning som f.eks. wind og humidity\
       -Værforhold på ønsket område\
         Grønn: bra forhold\
         Gul: middels forhold\
         Rød: dårlige forhold (ikke godkjent oppskytning)
 
 Alternativflyt:\
 2.1. Systemet mangler data for ønsket område\
 2.2. Brukeren blir bedt om å velge et annet område for oppskytning.
 
#### 3. Vi skal lage funksjonalitet for å endre og justere størrelsen på raketten for mer detaljerte tilbakemeldinger ved de andre kravene.
##### Tekstlig beskrivelse av Use Case - _"Endre spesifikasjoner på rakett":_

 Aktører:\
 -Bruker
 
 Prebetingelser:\
 -Bruker er pålogget applikasjonen.
 
 Postbetingelser:\
 -Endring av rakettens spesifikasjoner blir registrert og tatt med i beregningen av de andre funksjonelle kravene
 
 Hovedflyt:
 1. Bruker legger inn spesifikasjoner for raketten
 
#### 4. Lage funksjonalitet for å kontakte luftverns-tilsynet
##### Tekstlig beskrivelse av Use Case - _"Kontakt med luftvern-tilsyn"_:
 
 Aktører:\
 -Bruker\
 -Luftverns-tilsynet
 
 Prebetingelser:\
 -Bruker er pålogget applikasjonen.
 
 Postbetingelser:\
 -Bruker har opprettet kontakt med luftverns-tilsynet
 
 Hovedflyt:
 1. Bruker trykker på knapp for å kontakte luftverns-tilsynet
 2. Bruker skriver en melding med en overskrift
 3. Systemet genererer en e-post klar til å sendes til luftverns-tilsynet
 4. E-post om ønsket oppskytning sendes til luftverns-tilsynet
 
 Alternativflyt:
 
### Diagrammer
 
###### Sekvensdiagram - Visning av værdata
###### _Dette sekvensdiagrammet viser hva som skjer når bruker velger posisjon på et kart, og værdataen vises for bruker. For bedre lesbarhet er Screen og ViewModel kombinert. Diagrammet viser kun hva som skjer for WeatherForecast og ikke GRIB2._
```mermaid

sequenceDiagram

    actor Bruker
    participant MapScreen & ViewModel
    participant WeatherForecastScreen & ViewModel
    participant WeatherForecastRepository
    participant WeatherForecastDataSource
    participant met API

    Bruker->>MapScreen & ViewModel: Velg område på kart (pos)
    MapScreen & ViewModel->>MapScreen & ViewModel: updateMarkerPosition(pos)
    MapScreen & ViewModel-->>Bruker: oppdatert posisjon

    Bruker->>MapScreen & ViewModel: "Check Weather Forecast"-button
    MapScreen & ViewModel->>WeatherForecastScreen & ViewModel: navigate(screenName, lat, lon, alt)

    WeatherForecastScreen & ViewModel->>WeatherForecastRepository: getWeatherDetails(lat, lon)
    WeatherForecastRepository->>WeatherForecastDataSource: fetchWeather(lat, lon)

    WeatherForecastDataSource->>met API: client.get(url)
  
    alt suksess

        WeatherForecastDataSource-->>WeatherForecastRepository: Result<WeatherFeature>
        WeatherForecastRepository-->>WeatherForecastScreen & ViewModel: Result<WeatherFeature>
        WeatherForecastScreen & ViewModel-->>Bruker: Viser værdata for hver dag

    else feil
        WeatherForecastDataSource-->>WeatherForecastRepository: errorMessage:
        WeatherForecastRepository-->>WeatherForecastScreen & ViewModel: errorMessage:
        WeatherForecastScreen & ViewModel-->>Bruker: "No weather data available"

    end

```

###### Sekvensdiagram - Kontakt luftvern
###### _Dette sekvensdiagrammet viser funksjonaliteten for kontakt med luftverntilsynet_
```mermaid

sequenceDiagram

    actor Bruker
    participant Contact
    actor Luftvern

    Bruker->>Contact: Skriv en melding med overskrift
    Bruker->> Contact: Send melding

    alt suksess
        Contact-->>Luftvern: Melding til luftvern

    else feil
        Contact-->>Bruker: errorMessage

    end
```
###### Sekvensdiagram - Favoritt
###### _Dette sekvensdiagrammet viser funksjonaliteten for favorisering av tidspunkt_
```mermaid

sequenceDiagram

    actor Bruker
    participant WeatherForecastScreen & MainContent
    participant WeatherForecastViewModel
    participant FavoriteForecastViewModel
    participant FavoriteForecastRepository
    participant AppDatabase

    Bruker->>WeatherForecastScreen & MainContent: Trykk på favoriserikon (tidspunkt)

    alt favoritt icon er disabled
        WeatherForecastScreen & MainContent->>WeatherForecastViewModel: addFavoriteForecast(tidspunkt)
        WeatherForecastViewModel->>FavoriteForecastViewModel: addFavorite(favorite)
        FavoriteForecastViewModel->>FavoriteForecastRepository: insertFavorite(favorite)
        FavoriteForecastRepository->>AppDatabase: insertFavorite(favorite)
    
    else favoritt icon er enabled
        WeatherForecastScreen & MainContent->>WeatherForecastViewModel: removeFavoriteForecast(tidspunkt)
        WeatherForecastViewModel->>FavoriteForecastViewModel: removeFavorite(favorite)
        FavoriteForecastViewModel->>FavoriteForecastRepository: removeFavoriteById(favorite.compositeKey)
        FavoriteForecastRepository->>AppDatabase: removeFavoriteById(favorite.compositekey)

    end
```

###### Aktivitetsdiagram - Bruk av hovedfunksjonalitet
```mermaid
flowchart TD
    A((Start)) --> B(Velg posisjon)
    B --> D{Hent data}
    D -->|Finner ikke\ndata| B
    D --> F(Vis data for \nhver dag)
    F --> G(Velg dag \nog time)
    G --> H(Viser detaljert info for valgt\ntidspunkt i form av farger)
    H --> I{\n\n}
    I -->|Velg nytt tidspunkt| G
    I --> Z
    I -->|Favoriser tidspunkt| J(Tidspunkt favorisert)
    J --> Z(((Slutt)))
```

###### Klassediagram
###### _Dette er klassediagrammet for de viktigste klassene i appen. For lettere lesbarhet er noen klasser ikke inkludert, favoritescreen kommer etter som en egen del. (WF: forkortelse for WeatherForecast)._
```mermaid
classDiagram
    class WFDataSource{
        -client
        +Result fetchWeather(lat, lon)
    }
    class WFRepositoryImplementation{
        -WFDataSource
        -_cachedWeatherDetails
        -cachedWeatherDetails
        +override getWeatherDetails(lat, lon)
    }
    class WFRepository{
        <<interface>>
        +getWeatherDetails(lat, lon)
    }
    class WFAPI{
        String url
    }
    class WFUiState{
        +uiState
        +weatherFeature
        +timeSeries
        +errorMessage
        +forecastResultsMap
        +gridInfoMap
        +expandedItemIndices
        +favoriteItemIndices
        +mostRecentGridInfo
    }
    class WFViewModel{
        -WFRepository
        -gRIB2Repository
        -weatherDetailsCache
        -gridInfoCache
        -_wfUiState
        -wfUiState
        +initialize(lat, lon, alt)
        +updateMostRecentGridInfo(gridInfo)
        +addFavoriteForecast(time, ...)
        +removeFavoriteForecast(time, ...)
        +toggleItemExpanded(index)
        -toggleItemFavorited(index)
        -fetchWeatherDetails(lat, lon)
        -fetchGridInfo(lat, lon, alt)
        -updateWeatherDetailsState(weatherFeature)
        -updateGridInfoState(gridInfoMap)
        -populateEvalResult()
        -evaluateForecast(timeSeries, gridInfo)
        -StatusColor evaluateColor(maxValue, value)
        -StatusColor findWorstColor(statusColors)
    }
    class WFScreen{
        <<Composable>>
        +wfUiState
    }
    class HomeScreen{
        <<Composable>>
        +settingsRoute
        +mapRoute
    }
    class MapScreen{
        <<Composable>> 
        +latitudeInput
        +longitudeInput
        +initialLatLng
        +cameraPositionState
        +markerPosition
    }
    class FavoriteScreen{
        <<Composable>>
        +favoritesForecastUIState
        +favorites
        +settingsRoute
    }
    class SettingsScreen{
        <<Composable>>
        +isDarkThemeEnabled
    }
    class SettingsViewModel{
        +isDarkThemeEnabled
        -dataStore
        +toggleDarkTheme(isEnabled)
    }
    class SetupNavHost{
        <<Composable>>
        +homeScreenRoute
        +mapScreenRoute
        +settingsScreenRoute
        +WFScreenRoute
        +favoritesScreenRoute
        +contactScreenRoute
    }
    class GRIB2DataSource{
        -client
        +fetchGRIB2Files()
        -fetchGRIB2()
    }
    class GRIB2Repository{
        <<interface>>
        +getGrib2Info(lat, lon, alt)
    }
    class GRIB2RepositoryImplementation{
        -GRIB2DataSource
        -_cachedGridInfoMap
        -cachedWeatherDetails
        +override getGrib2Info(lat, lon, alt)
        -processGrib2File(gds, lat, lon, alt)
        -calculateAltitude(pressure, temp)
        -getWindVectorsAtAltitude(gds, lat, lon, alt)
        -calculateMaxWindShear(gds, lat, lon, alt)
        -calculateMaxWind(gds, lat, lon, alt)
    }
    class GRIBAPI{
        String url
    }

    SetupNavHost <--> WFScreen
    SetupNavHost <--> HomeScreen
    SetupNavHost <--> MapScreen
    SetupNavHost <--> SettingsScreen
    SetupNavHost <--> FavoriteScreen
    SettingsScreen --> SettingsViewModel

    WFScreen --> WFUiState
    WFViewModel <-- WFUiState
    WFDataSource --> WFAPI
    
    GRIB2DataSource --> GRIBAPI
    GRIB2RepositoryImplementation --> GRIB2DataSource
    GRIB2RepositoryImplementation <-- WFViewModel
    GRIB2RepositoryImplementation .. GRIB2Repository
    
    WFRepositoryImplementation --> WFDataSource
    WFRepositoryImplementation <-- WFViewModel
    WFRepositoryImplementation .. WFRepository
```
```mermaid
classDiagram
    class AppDataBase{
        <<abstract class>>
        favoriteForecastItemDao()
    }
    class FavoriteForecastItemDao{
        <<interface>>
        insertFavorite(favoriteForecastItem)
        getAllFavorites()
        removeFavoriteById(id)
    }
    class FavoriteForecastRepository{
        -favoritesDao
        +getAllFavorites()
        +insertFavorite(favorite)
        +removeFavoriteById(id)
    }
    class FavoriteForecastUiState{
        +expandedItemIndices
        +favoritedItemIndices
        +favorites
    }
    class FavoriteViewModel{
        -favoritesRepository
        -isInit
        -_favoriteForecastUiState
        +favoriteForecastUiState
        +addFavorite(favorite)
        +removeFavorite(compositeKey)
        +toggleItemExpanded(compositeKey)
        +toggleItemFavorited(compositeKey)
        -loadFavorites()
    }
    class FavoriteScreen{
        <<Composable>>
        +favoritesForecastUIState
        +favorites
        +settingsRoute
    }
    SetupNavHost <--> FavoriteScreen
    AppDataBase .. FavoriteForecastItemDao
    AppDataBase <-- FavoriteForecastRepository
    FavoriteViewModel --> FavoriteForecastRepository
    FavoriteViewModel <-- FavoriteForecastUiState
    FavoriteScreen --> FavoriteForecastUiState
```
