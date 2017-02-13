# Swedish translation, last updated in sync with Messages.properties.en,v 1.31

language_name: Svenska
more_options: Fler val
title_more_options: Visa/Dölj andra valideringsval
all: Alla
normal_report: Normal rapport
most_important: Mest viktigt
no_warnings: Inga varningar
profile: Profil
no_special_profile: Ingen speciell profil
css1: CSS nivå 1
css2: CSS nivå 2
css21: CSS nivå 2.1
css3: CSS nivå 3
SVG: SVG
svgbasic: SVG Basic
svgtiny: SVG Tiny
SVG_basic: SVG Basic
SVG_tiny: SVG Tiny
mobile: Mobil
ATSC_TV_profile: ATSC TV-profil
TV_profile: TV-profil
medium: Medium
aural: ljud
braille: braille
embossed: präglad
handheld: handhållen
print: skriv ut
projection: projicering
screen: skärm
tty: TTY
tv: TV
presentation: presentation
type: Typ
no_special_type: Automatisk
html_doc: HTML
css_doc: CSS
check: Granska
W3C_CSS_validation_service: W3C:s CSS-valideringstjänst
check_CSS: Granska Cascading Style Sheets (CSS) och (X)HTML-dokument med formatmallar
by_URI: Genom URI
by_file_upload: Genom filuppladdning
by_direct_input: Genom direktinmatning
validate_by_URI: Validera genom URI
enter_uri: Mata in URI för det dokument (HTML med CSS eller enbart CSS) som du vill validera
page_address: Adress för den sida som skall valideras
address: Adress
submit_uri: Skicka in URI för validering
validate_by_file_upload: Validera genom att ladda upp fil
choose_document: Välj det dokument som du vill validera
choose_local_file: Välj en lokalt lagrad fil att ladda upp och validera
local_CSS_file: Lokal CSS-fil
submit_file: Skicka in fil för validering
validate_by_input: Validera genom direkt inmatning
enter_CSS: Mata in den CSS du vill validera
submit_input: Skicka in data för validering
note: Märk
note_xhtml_valid: Om du vill validera din CSS-formatmall inbäddad i ett (X)HTML-dokument, så bör du först <a href="http://validator.w3.org/">kontrollera att den (X)HTML du använder är giltig</a>
# End of variables added along with Index page template

W3C_validator_results: W3C CSS-valideringsresultat för
# file_title: <!-- provided by the user -->
CSS_validation_service: CSS-valideringstjänst
jump_to: Hoppa till
# errors_count: <!-- generated on the fly -->
errors: Fel
# warnings_count <!-- generated on the fly -->
warnings: Varningar
validated_CSS: Validerad CSS
congrats: Gratulerar! Inga fel har hittats
# The sentence defined by doc_validates_before_link and doc_validates_after_link variables goes: \
# "This document validates as CSSXX!" where XX stands for the CSS version used \
# Since the content of the link about the CSS version is generated on the fly, \
# it is important to define what goes before the link and what goes afterwards in 2 separate \
# variables because the position of the link in the sentence varies in different languages
doc_validates_before_link: Detta dokument valideras som
doc_validates_after_link: !
no_errors_interoperable_msg: För att visa dina läsare att du har lagt dig vinn om att skapa en interoperabel \
webbsida, så kan du visa denna ikon på alla sidor som valideras. Här är den XHTML \
du kan använda för att lägga till denna ikon på din webbsida:
valid_CSS: Valid CSS!
# <!-- close the img tag with > instead of /> if using HTML<= 4.01 -->
no_errors_close_tags_msg: avsluta img-taggen med &gt; iställetr för med /&gt; om du använder HTML &lt;= 4.01
no_errors_dl_local_msg: Om du vill så kan du ladda ner en kopia av denna bild och spara den i din lokala webb \
katalog, och ändra XHTML-fragmentet ovan så att det refererar till din lokala bild \
istället för bilden på denna server.
no_errors_create_link_msg: Om du vill skapa en länk till denna sida (dvs detta validerings-\
resultat) för att göra det lättare att validera denna sida i framtiden, eller för att låta \
andra validera dina sidor, använd då URI:en: 
no_errors_forHTML_only_msg: enbart för HTML-/XML-dokument
no_errors_bookmark_msg: Eller så kan du helt enkelt lägga till denna sida till dina bokmärken eller snabblänkar.
note_valide_html: För att fungera som avsett, så behöver din CSS-formatmall ett korrekt dokumentparseträd. Detta betyder att du bör använda <a href="http://validator.w3.org/">valid HTML</a>.
top: Toppen
# hook_html_validator <!-- generated on the fly -->
not-css1-style: /* VAR FÖRSIKTIG! Detta är inte en CSS1-egenskap! */
errors_sorry_msg: Ledsen! Vi har hittat följande fel
# errors_list: <!-- generated on the fly -->
# warnings_list: <!-- generated on the fly -->
# rules_count: <!-- generated on the fly -->
valid_CSS_info: Valid CSS-information
# charset: <!-- generated on the fly -->
# rules_list: <!-- generated on the fly -->
CSS_not_found: Ingen formatmall upptäcktes
home: Hem
about: Om
doc: Dokumentation
dl: Ladda ner
feedback: Synpunkter
credits: Tack till
home_title: Hemsida för W3C:s CSS-valideringstjänst
about_title: Om denna tjänst
doc_title: Dokumentation om W3C:s CSS-valideringstjänst
dl_title: Ladda ner och installera CSS-valideraren
feedback_title: Hur man kan ge synpunkter på denna tjänst
credits_title: Hjälp och tack
W3C_quality_msg: W3C:s kvalitetssäkringsaktivitet, ger dig gratis verktyg för webbkvalitet och annat
learn_more_msg: Lär dig mer om Cascading Style Sheets
support: Stöd detta verktyg, bli
supporter: W3C Supporter
# End of variables added along with Velocity

# Defines your own error and warning message here
content-type: text/html; charset=utf-8
content-language: sv
output-encoding-name: utf-8

# You can change the level warning like this (example) :
# warning.redefinition.level: 5
# level is an integer between 0 and 9 (all others values are ignored)
warning.redefinition: Omdefiniering av %s

# used by xml parser 
warning.style-inside-comment: Lägg inte stilregler inom HTML-kommentarer, eftersom de kan tas bort av användaragenten

# used by org.w3c.css.properties.Css1Style
warning.same-colors: Samma färg för %s och %s
warning.no-color: Du har inte angett color (eller color anges som genomskinlig) men du har angett en background-color. Försäkra dig om att färgkaskader inte gör texten oläslig.
warning.no-background-color: Du har inte angett background-color (eller background-color är angedd som genomskinlig) men du har angett color. Försäkra dig om att färgkaskader inte gör texten oläsbar.
#warning.color.mixed-capitalization is now obsolete
#warning.color.mixed-capitalization: Although color names are case-insensitive, it is recommended to use the mixed capitalization, to make the names more legible: %s
warning.no-generic-family: %s: Du borde erbjuda en generisk familj som sista alternativ
warning.with-space: Familjenamn som innehåller vita tecken skall anges inom citationstecken. Om citationstecken \
utlämnas, så kommer inledande och avslutande vita tecken att ignoreras, och sekvenser av vita \
tecken inom namnet att omvandlas till ett blanktecken. 
warning.no-padding: Du bör ha ett utfyllnadsområde med bakgrundsfärg
warning.same-colors2: Samma färg för color och background-color i två kontext %s och %s
warning.relative-absolute: Du har använt både absolut och relativ längd i %s. Detta är inte en robust formatmall.
# used by org.w3c.css.properties.CssSelectors
warning.unknown-html: %s är inte ett HTML-element
warning.html-inside: HTML-element kan inte förekomma inom ett annat element
warning.body-inside: BODY-element kan inte vara inom ett annat element, förutom HTML
warning.pseudo-classes: Anchor-pseudoklassen %s har ingen effekt på element andra än 'A'

# not used by org.w3c.css.properties.CssSelectors for the moment
warning.noinside: %s kan inte förekomma inom ett inline-element
warning.withblock: Var försiktig. Pseudoelement kan bara knytas till blocknivåelement
warning.block-level: Denna egenskap har effekt för blocknivåelement.

# used by org.w3c.css.parser.Frame
warning.no-declaration: Ingen deklaration i regeln

# used by org.w3c.css.parser.CssFouffa
warning.unsupported-import: Importerade formatmallar granskas inte vid direkt inmatning eller vid filuppladdning

# used by org.w3c.css.values.CssColor
warning.out-of-range: %s ligger utanför giltigt värdeområde
error.invalid-color: Ogiltig RGB-funktion

warning.marker: Egenskapen marker-offset är bara tillämpbar på element med 'display: marker'

# used by org.w3c.css.properties.ACssStyle
warning.relative: Relativa enheter ger mer robusta formatmallar i egenskap %s

# used by org.w3c.css.css.StyleSheetParser and org.w3c.css.css.StyleSheetXMLParser
error.at-rule: Tyvärr, at-regeln %s är inte implementerad.

# used by all properties and values
error.operator: %s är en ogiltig operator
error.negative-value: %s negativa värden är inte tillåtna
error.few-value: alltför få värden för egenskap %s

# be careful here, values comes first
# You can't write something like this : For the color, blue is an incorrect value
error.value: %s är inte ett %s-värde

#used by org.w3c.css.properties3.CssToggleGroup
error.groupname: %s är inte ett giltigt gruppnamn. Använd en giltig identifierare

#used by org.w3c.css.properties3.CssGroupReset
error.nogroup: %s har inte satts av egenskapen toggle-group

#used by org.w3c.css.properties3.CssGlyphOrVert
error.anglevalue: Värdet måste vara mellan -360 och 360, och vara delbart med 90

#used by org.w3c.css.properties3.CssTextKashidaSpace
error.percentage: procentvärde förväntades

#used by org.w3c.css.properties.CssTextAlign
warning.xsl: värde %s är bara användbart för XSL

#used by org.w3c.css.parser.analyzer.CssParser
warning.medialist: medialista skall börja med 'media :' %s
error.nocomb: Kombinator %s mellan selektorer kan inte användas i denna profil eller version

#used by org.w3c.css.properties.CssDirection
warning.direction: istället för att använda 'direction' för blocknivåelement, använd den nya CSS3-egenskapen 'writing-mode'

# used by org.w3c.css.properties.CssTextDecoration
error.same-value: %s förekommer två gånger

error.generic-family.quote: Generiska familjenamn är nyckelord, och får därför inte anges inom citationstecken.

# used by org.w3c.css.properties.CssClip
error.shape: Ogiltig formdefinition rect(<top>,<right>,<bottom>,<left>)
error.shape-separator: Ogiltig separator i formdefinition. Det måste vara ett kommatecken.

# used by org.w3c.css.properties.CssContent
error.attr: Ogiltig attr-definition attr(X)
error.function: Ogiltig funktionsdefinition
error.counter: Ogiltig counter-definition counter(<identifier>[,<list-style-type>]?)
error.counters: Ogiltig counters-definition counters(<identifier>,<string>[,<list-style-type>]?)

# used by org.w3c.css.font.Src
error.format: Ogiltig format-definition format(<string>[,<string>]*)
error.local: Ogiltig formatdefinition local(<string>|<ident>+)

# used by org.w3c.css.values.CssAngle, org.w3c.css.values.CssFrequency, org.w3c.css.values.CssTime, org.w3c.css.values.CssLength
error.unit: %s är en ogiltig enhet

# used by org.w3c.css.aural.ACssAzimuth
error.degree: Position måste anges i grader.

# used by org.w3c.css.aural.ACssElevation
error.elevation.range: Anger elevation som vinkel, mellan '-90deg' och '90deg'.

# used by org.w3c.css.aural.ACssPitchRange
error.range: Värdet ligger utanför giltigt värdeområde. Detta värde måste vara mellan '0' och '100'.

# used by org.w3c.css.properties.CssTextShadow
error.two-lengths: Ett skuggoffset anges med två <length>-värden (en suddradie kan anges efter skuggoffset.)

error.integer: Detta tal måste vara ett heltal.
error.comma: Kommatecken saknas.

# used by org.w3c.css.values.CssPercentage
error.percent: %s är en ogiltig procentsats

# used by org.w3c.css.values.CssString
error.string: %s är en ogiltig sträng

# used by org.w3c.css.values.CssURL
error.url: %s är en ogiltig URL

# used by org.w3c.css.values.CssColor
error.rgb: %s är inte en giltig färgangivelse som 3- eller 6-siffrigt hexadecimaltal
error.angle: %s är inte en giltig vinkel. Värdet måste vara mellan 0 och 360

# used by org.w3c.css.values.CssNumber
error.zero: endast 0 kan vara en %s. Du måste ange en enhet efter ditt tal

# used by org.w3c.css.parser.CssPropertyFactory
error.noexistence-at-all: Egenskap %s finns inte
error.noexistence-media: Egenskap %s finns inte för medium %s
error.noexistence: Egenskap %s finns inte för %s men finns för %s
warning.noexistence: Egenskap %s finns inte för %s men finns för %s
warning.noexistence-media: Egenskap %s finns inte för medium %s
warning.notforusermedium : Egenskap %s finns inte för detta usermedium
warning.noothermedium : Egenskaper för andra media kanske inte fungerar för user-medium
# used by org.w3c.css.parser.AtRule*
error.noatruleyet: Andra @-regler än @import stöds inte av CSS1 %s
# used by org.w3c.css.parser.analyzer.CssParser
error.notforcss1: Värde %s finns inte i CSS1
warning.pseudo: Okänt pseudoelement eller pseudoklass %s i default-profilen (%s)
warning.nocomb: Kombinator %s mellan selektorer tillåts inte i denna profil (%s)
warning.charsetspecial: Denna profil har en mycket speciell syntax för @charset: \
@charset följt av exakt ett blanktecken, följt av kodnamnet \
inom citationstecken, omedelbart följt av ett semikolon.
warning.notversion: %s kan inte användas med denna version av CSS: %s

# used by org.w3c.css.parser.CssFouffa
error.unrecognize: Alltför många värden eller värden kan kännas igen

# used by org.w3c.css.parser.CssFouffa
generator.unrecognize: Parsningsfel

# used by org.w3c.css.css.StyleSheetGeneratorHTML2
error.unknown: Okänt fel

# used by org.w3c.css.parser.CssSelectors
error.pseudo-element: Pseudoelement :%s kan inte förekomma här i kontextet %s
error.pseudo-class: Pseudoklass .%s kan inte förekomma här i HTML-kontextet %s
error.pseudo: Okänt pseudoelement eller pseudoklass %s
error.id: ID-selektor #%s ogiltig ! Endast en ID-selektor kan anges i en enkel selektor: %s.
error.space: Om attributselektor ~= används, så får ordet i värdet %s inte innehålla blanktecken.
error.todo : Tyvärr är funktionalitet %s ännu inte implementerad.
error.incompatible: %s och %s är inkompatibla
warning.incompatible: %s och %s är inkompatibla
error.notformobile: %s kan inte användas med mobil profil
error.notforatsc: %s kan inte användas med ATSC-profil
error.notfortv: %s kan inte användas med TV-profil
error.notversion: %s kan inte användas med denna version av CSS : %s

error.media: icke igenkänt medium %s
error.page: icke igenkänd pseudonamnsida %s

error.unrecognized.link: Icke igenkänt länkelement eller xml-formatmall PI.

# used by StyleSheetGeneratorHTML
generator.context: Kontext
generator.request: Ett fel inträffade vid utmatning av din formatmall. \
Korrigera din begäran eller sänd ett e-postmeddelande till plh@w3.org.
generator.unrecognized: Icke igenkänd
generator.invalid-number: Ogiltigt tal
generator.property: Värdefel
generator.line: Rad
generator.not-found: Fil kunde inte hittas

generator.doc-html: <!-- tog bort detta förvirrande meddelande olivier 2006-12-14 -->
generator.doc: <!-- tog bort detta förvirrande meddelande olivier 2006-12-14 -->


# used by the parser
parser.semi-colon: försök att hitta ett semikolon före egenskapsnamn. Lägger till ett

parser.old_class: I CSS1 kan ett klassnamn börja med en siffra (".55ft"), \
om det inte är en dimension (".55in"). I CSS2 parsas sådana klasser som \
okända dimensioner (för att göra det möjligt att i framtiden lägga till nya enheter) \
För att göra ".%s" till en giltig klass så kräver CSS2 att den första siffran särmarkeras ".\3%s"

parser.old_id: I CSS1 kan ett id-namn börja med en siffra ("#55ft"), \
om det inte är en dimension ("#55in"). I CSS2 parsas sådana id:ar som \
okända dimensioner (för att göra det möjligt att i framtiden lägga till nya enheter)

parser.class_dim: I CSS1 kan ett klassnamn börja med en siffra (".55ft"), \
om det inte är en dimension (".55in").

parser.id_dim: I CSS1 kan ett id-namn börja med en siffra ("#55ft"), \
om det inte är en dimension ("#55in").

parser.charset: @charset-regeln kan endast förekomma i början av en \
formatmall. Kontrollera att det inte finns blanktecken före den.

parser.charsetspecial: Denna profil har en mycket speciell syntax för @charset: \
@charset följt av exakt ett blanktecken, följt av kodningsnamnet \
inom citationstecken, omedelbart följt av ett semikolon.

warning.old_id: I CSS1 kan ett id-namn börja med en siffra ("#55ft"), \
om det inte är en dimension ("#55in"). I CSS2 parsas sådana id:ar \
som okända dimensioner (för att göra det möjligt att i framtiden lägga till nya enheter)

warning.old_class: I CSS1 kan ett klassnamn börja med en siffra ("#55ft"), \
om det inte är en dimension ("#55in"). I CSS2 parsas sådana id:ar som \
okända dimensioner (för att göra det möjligt att i framtiden läga till nya enheter).

# used by the servlet
servlet.invalid-request: Du har sänt en ogiltig begäran.
servlet.process: Kan inte bearbeta objektet

warning.atsc: %s kanske inte stöds av medium atsc-tv
error.onlyATSC: %s denna funktion enbart avsedd för medium atsc-tv

warning.otherprofile: egenskap %s finns inte i denna profil, men valideras mot annan profil
warning.deprecated: detta värde är föråldrat

parser.unknown-dimension: Okänd dimension
generator.dontmixhtml: Parsningsfel: Formatmallar bör inte innehålla HTML-syntax.

warning.float-no-width: In (x)HTML+CSS, floated elements need to have a width declared. Only elements with an intrinsic width (html, img, input, textarea, select, or object) are not affected <!-- translation unavailable -->