package info.rsdev.mysite.writing.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Singleton;

import info.rsdev.mysite.writing.domain.Document;
import info.rsdev.mysite.writing.domain.Part;
import info.rsdev.mysite.writing.domain.Version;

/**
 * This implementation is for mockup purposes, so we can work on the domain / UI before the data layer is finished and filled with
 * documents.
 */
@Singleton
public class StaticReadingDao implements IReadingDao {
    
    private final ArrayList<Document> documents = new ArrayList<>();
    
    public StaticReadingDao() {
        documents.add(makeErWasEens());
        documents.add(makeStoppen());
    }
    
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    @Override
    public Document getDocumentByName(String name) {
        for (Document candidate: documents) {
            if (candidate.getTechnicalName().equals(name)) {
                return candidate;
            }
        }
        return null;
    }
    
    private Document makeErWasEens() {
        Document document = new Document(new Locale("nl"), "Er was eens...");
        Version onlyVersion = document.newVersion("original");
        onlyVersion.setAuthor("David Es").setAuthorEmailAddress("dave@cyber-d.com");
        onlyVersion.setTitle("Er was eens...");
        onlyVersion.setTeaser("Dit is het eerste verhaaltje dat ik geschreven heb, op de achterkant van een bierviltje. "
                + "Tijdens het overtypen in de tekstverwerker liep het allemaal uit de hand. Geïnteresseerd? Klik dan "
                + "hieronder om mijn eerste, voorzichtige ontsporingen te lezen.");
        Part firstPage = onlyVersion.newContentPart();
        firstPage.setTitle("Over een bij in de wei");
        firstPage.setText("Er was eens een bijtje dat zoemde boven velden en beekjes. Zijn vleugels waren sterk, "
                + "maar zijn gezicht zag bleekjes. Teveel nectar in zijn knar, dat maakte zijn buik zo weekjes.");
        document.publishVersion(onlyVersion.getVersionName());
        return document;
    }
    
    private Document makeStoppen() {
        Document document = new Document(new Locale("nl"), "Stoppen");
        Version onlyVersion = document.newVersion("original");
        onlyVersion.setAuthor("David Es").setAuthorEmailAddress("dave@cyber-d.com");
        onlyVersion.setTitle("Stoppen");
        Part wholeStory = onlyVersion.newContentPart();
        wholeStory.setText( 
                "Negenenveertig dagen duurt deze hel, op de kop af negenenveertig fucking dagen. Het bloed tintelt door mijn aderen en de onrust bonkt in mijn spieren. Wild flikker ik mijn fiets van me af; ze stuitert omhoog over een molshoop en valt ter aarde. Ik beeld me in dat ze Marjan is, en haal hard met mijn voet uit, tegen het achterwiel. Godverdommese kutwijf! Au, kut, dat doet zeer. Ik kijk instinctief om me heen of niemand me gezien heeft en pak mijn voet vast om de pijn weg te masseren. Ik laat me op de grond zakken en wieg zachtjes heen en weer, in een poging mezelf te kalmeren.\n" + 
                "Ik probeer na te denken over de ruzie die we net gehad hebben. De zoveelste de afgelopen weken. Mijn hoofd is gevuld met watten en ik krijg geen vat op mijn herinneringen. Ik hoor alleen nog haar verwijten en haar schorre gekrijs. Die kut weet gewoon niet waar ik doorheen ga. Zij heeft nooit gerookt. Maar als je ziet dat mijn stoppen doorslaan, dan blijf je me toch niet op mijn nek zitten? Dan vraag je er toch om? Teringtyfusteef. Ik zie weer mijn vlakke hand uithalen, hoe ze met haar zij tegen de punt van de teevee valt en met beide handen haar buik probeert te beschermen, de angst in haar ogen. En toen ben ik weggelopen, nee, weggerend, het huis uit. Ik ben op mijn fiets gesprongen en als een bezetene het dorp uit geracet. Ik wil niemand zien, rust. Dus nu ben ik hier, in de weilanden, ingezoomd tussen de snelweg en de Kaapse bosjes. Ik kom hier altijd wanneer ik stoom moet afblazen. De weilanden zijn vredig. De auto's die op grote afstand langsrazen rustgevend. Bovendien is het rustig nu, de meeste mensen zitten thuis kassie te kijken of liggen al op bed.\n" + 
                "Maar ik kom niet tot rust. Ik voel me erin geluisd. Ik wil geen vader worden en ik wil niet stoppen met roken. Geduldig heeft ze haar greep op mijn leven in kleine stapjes versterkt, mijn wezen verstikt, tot er niets meer van mijzelf overblijft. Uit mijn kontzak haal ik mijn knipmes tevoorschijn. Ik open hem en sluit hem, open, dicht, open, dicht, open. Ik stroop de mouw van mijn jas omhoog en leg het koude metaal van het lemmet op mijn bovenarm. Een trage beweging, een stroompje bloed. Ik voel mijn hart krachtig bonken in mijn borstkas. Het lijkt of er  weer leven terug mijn lijf invloeit, of ik weer van mezelf wordt. “Aaahhhh!” ik schreeuw het uit naar de donkere lucht.\n" + 
                "Ik sta op en schreeuw nogmaals, langgerekt, en nog een keer. Ik wordt licht in mijn hoofd van het schreeuwen. Schreeuwen werkt bevrijdend. Ik merk dat ik voor het hek sta dat toegang geeft tot de weilanden. Ik voel me uitgeput en een stuk rustiger. Mijn handen rusten op het houten hek, het koude houten hek, het mes nog in mijn hand. Ik stop het mes terug in mijn kontzak. Wat ben ik een dwaas. Ik moet mijn leven weer in eigen hand nemen. Ik wil niet terugvallen in mijn oude fouten en me weer laten overvleugelen.\n" + 
                "Dan zie ik in de verte een rode stip oplichten en langzaam weer wegsterven. In een reflex trekken mijn longen vacuüm: een sigaret. Ik stap door het openstaande hek het fietspad op, omdat de rij knotwilgen mij mijn zicht ontneemt. Weer licht de stip op, dichterbij nu. Als ze vlakbij is, spring ik zonder de consequenties te overdenken  naar voren. Ik wil niet dat ze me ontglipt. Een hoop gegil, de peuk valt op de grond en ik hou met beide vuisten het stuur van de fiets stevig omklemd. “Godverdomme klootzak, wat moet je van me!” roept het meisje dat bij de sigaret hoort me toe. Ik kijk haar aan. Een tenger meisje van een jaar of zeventien, hooguit achttien, zit stoer op haar omafiets, een kort zwart rokje over een donkergrijze maillot en twee grote zilveren oorringen in haar oor. Ze draagt een glanzend zwarte, gewatteerde jas en haar lange blonde krulhaar valt royaal over haar kraag van imitatiebont. Om haar schouder hangt een mini handtasje, nauwelijks groter dan een forse portemonnee, aan een smal bandje. Ze is opgemaakt, misschien komt ze net terug van een afspraakje. Haar blik, angstig, maar ook verwaand. Haar kin omhoog, uitdagend bijna. “Wat moet je” vraagt ze opnieuw, haar stem trilt. Ik voel een vreemde druk vanuit mijn scrotum mijn lichaam intrekken. “Meekomen”, commandeer ik en trek aan haar stuur om richting te geven, maar ze zet zich schrap. Ik taxeer kort haar weerstand. Dan haal ik uit met mijn vlakke hand. Haar gezicht zwiept naar rechts en ze verliest bijna haar evenwicht. Ze begint te jammeren en ik herhaal “Meekomen smerige slet, als je dit wilt overleven”. Ze stapt gedwee af en volgt me snikkend naar de weilanden met de fiets in haar hand. Adrenaline giert door mijn lijf. Mijn mond trekt, een grimas, en ik bijt mijn kiezen stevig op elkaar. Ik voel een duistere kracht in mijn borst gonzen, zoals ik nooit eerder heb gevoeld. We lopen zwijgend één, twee weilanden door en bij het hek tussen twee weilanden vind ik het welletjes. Als ik eerlijk ben, heb ik geen idee waar ik mee bezig ben. Maar het voelt zo machtig goed, dat ik me er aan overgeef. “Ga tegen het hek staan, met je rug naar me toe” hijg ik.\n" + 
                "Ik ga vlak achter haar staan en sjor haar jas en rok omhoog. Ze wil zich omdraaien, maar ik hou haar tegen. Ik druk mijn jukbeen van opzij tegen haar hoofd en grom “Ik ga je neuken, kut, zoals je nog nooit geneukt bent. En als je je gedraagt, mag je daarna weer naar mammie”. Met mijn rechterhand haal ik mijn knipmes uit mijn kontzak, en zet het lemmet tegen haar wang. “Maar als je lastig bent, maak ik korte metten met je.” Ze snikt en zwijgt. Dat vat ik op als een bevestiging dat ze meewerkt. Ik stroop haar maillot omlaag. Om beide handen vrij te hebben berg ik het knipmes op in mijn jaszak. Ik stroop de maillot tot op haar enkels omlaag, wip de pump van haar rechtervoet en laat de maillot van haar voet glijden, zodat ze haar benen kan spreiden. Mijn middelvinger dringt bij haar naar binnen om te voelen of ze nat genoeg is. Haar adem stokt en ze jammert en jankt luider nu. Maar ze is wel kletsnat, de vuile slet. Gehaast trek ik mijn broek omlaag en duw ruw mijn pik bij haar naar binnen. Het meisje hangt over het hek en snottert. “Nee, stop, stop, nee” kermt ze met een hoog stemmetje.\n" + 
                "Dus jij wil dat ik stop, smerige hoer? Ik pomp krachtig in haar op en neer. Jij hebt niks te willen. Ik stop pas wanneer ik dat wil. Ik ben hier de baas en dat zal je weten ook. Mijn vuist heeft zich in haar blonde krullen vastgezet en trekt haar hoofd naar achteren. Ik sluit mijn ogen en stel me voor dat Marjan voor me tegen het hek geleund staat. Ik fantaseer dat het Marjan’s billen zijn waar mijn ballen tegenaan ketsen. En vlak voordat ik mijn zaad in haar afvuur, haal ik mijn knipmes te voor­schijn. “Ik”, “wil”, “geen”, “vader”, “worden”, stoot ik bij elk woord en met een krachtige haal snij ik haar keel door. Mijn vuist houdt nog steeds haar hoofd vast, maar ik ben klaar met haar. Ik werp haar opzij. Als een lekke opblaaspop zakt ze langs het hek in elkaar, haar armen spartelend langs haar zij. Ze maakt vreemde, gorgelende geluiden. Ik trek mijn broek omhoog en ga op de grond zitten. Mijn blik rust op haar. Ze ligt wild te schokken. “Oh ja” mompel ik hardop en neem haar hand­tasje van haar schouder. Ik haal een pakje Camel sigaretten eruit en een aansteker. Met een diepe teug vul ik mijn longen met warme rook. Zo, daar was ik aan toe.");
        document.publishVersion(onlyVersion.getVersionName());
        return document;
    }
    
}
