package pheninux.xdev.ticketcompare.util;

import java.util.*;

/**
 * Classe utilitaire pour la catégorisation automatique des produits
 */
public class ProductCategories {

    /**
     * Liste complète des catégories disponibles
     */
    public static final List<String> ALL_CATEGORIES = Arrays.asList(
        "Fruits et Légumes",
        "Viandes et Poissons",
        "Produits Laitiers",
        "Fromages",
        "Boulangerie",
        "Pâtisserie",
        "Épicerie Salée",
        "Épicerie Sucrée",
        "Céréales et Féculents",
        "Pâtes et Riz",
        "Conserves",
        "Sauces et Condiments",
        "Huiles et Vinaigres",
        "Boissons",
        "Boissons Alcoolisées",
        "Surgelés",
        "Snacks et Apéritifs",
        "Confiserie et Chocolat",
        "Petit-Déjeuner",
        "Biscuits et Gâteaux",
        "Produits Bio",
        "Produits Sans Gluten",
        "Produits Végétariens",
        "Produits pour Bébé",
        "Hygiène et Beauté",
        "Entretien de la Maison",
        "Animalerie",
        "Papeterie",
        "Jardinerie",
        "Bricolage",
        "Textile",
        "Autre"
    );

    /**
     * Map des mots-clés pour chaque catégorie
     */
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        // Fruits et Légumes
        CATEGORY_KEYWORDS.put("Fruits et Légumes", Arrays.asList(
            "pomme", "poire", "banane", "orange", "citron", "pamplemousse", "mandarine", "clémentine",
            "raisin", "fraise", "framboise", "myrtille", "cerise", "prune", "abricot", "pêche", "peche",
            "nectarine", "kiwi", "mangue", "ananas", "melon", "pastèque", "pasteque", "figue",
            "tomate", "carotte", "pomme de terre", "oignon", "ail", "échalote", "echalote", "poireau",
            "courgette", "aubergine", "poivron", "concombre", "salade", "laitue", "mâche", "mache",
            "épinard", "epinard", "chou", "brocoli", "chou-fleur", "haricot", "petit pois", "pois",
            "radis", "navet", "betterave", "céleri", "celeri", "fenouil", "artichaut", "asperge",
            "champignon", "avocat", "courge", "potiron", "citrouille", "endive", "persil", "basilic",
            "menthe", "coriandre", "légume", "legume", "fruit", "bio vrac"
        ));

        // Viandes et Poissons
        CATEGORY_KEYWORDS.put("Viandes et Poissons", Arrays.asList(
            "viande", "bœuf", "boeuf", "veau", "porc", "agneau", "mouton", "poulet", "volaille",
            "dinde", "canard", "oie", "pintade", "lapin", "steak", "côte", "cote", "rôti", "roti",
            "escalope", "filet", "bavette", "entrecôte", "entrecote", "jarret", "gigot", "épaule", "epaule",
            "saucisse", "merguez", "chipolata", "knack", "jambon", "lardons", "bacon", "chorizo",
            "saucisson", "pâté", "pate", "terrine", "rillettes", "andouille", "boudin",
            "poisson", "saumon", "truite", "cabillaud", "morue", "thon", "sardine", "maquereau",
            "dorade", "bar", "loup", "sole", "merlu", "colin", "raie", "lieu", "aiglefin",
            "crevette", "gambas", "homard", "langouste", "crabe", "tourteau", "bulot", "huître", "huitre",
            "moule", "coquille", "saint-jacques", "calmar", "seiche", "poulpe"
        ));

        // Produits Laitiers
        CATEGORY_KEYWORDS.put("Produits Laitiers", Arrays.asList(
            "lait", "demi-écrémé", "demi-ecreme", "entier", "écrémé", "ecreme", "sans lactose",
            "yaourt", "yogurt", "fromage blanc", "petit suisse", "faisselle", "crème", "creme",
            "crème fraîche", "creme fraiche", "crème liquide", "crémeux", "cremeux", "dessert lacté",
            "flan", "mousse", "crème dessert", "liégeois", "liegeois", "riz au lait", "semoule au lait",
            "beurre", "margarine", "lait de chèvre", "lait de chevre", "lait de brebis"
        ));

        // Fromages
        CATEGORY_KEYWORDS.put("Fromages", Arrays.asList(
            "fromage", "camembert", "brie", "roquefort", "comté", "comte", "emmental", "gruyère", "gruyere",
            "cantal", "reblochon", "raclette", "morbier", "beaufort", "abondance", "tomme",
            "chèvre", "chevre", "bûche", "buche", "crottin", "rocamadour", "picodon", "sainte-maure",
            "bleu", "gorgonzola", "stilton", "fourme", "parmesan", "pecorino", "mozzarella",
            "feta", "ricotta", "mascarpone", "grana padano", "mimolette", "edam", "gouda", "maasdam"
        ));

        // Boulangerie
        CATEGORY_KEYWORDS.put("Boulangerie", Arrays.asList(
            "pain", "baguette", "tradition", "campagne", "complet", "céréales", "cereales", "seigle",
            "mie", "pain de mie", "brioche", "croissant", "pain au chocolat", "chocolatine",
            "viennoiserie", "pain aux raisins", "chausson", "chouquette", "panettone", "pain burger",
            "pain hot dog", "pain pita", "tortilla", "wrap", "naan", "ciabatta", "focaccia"
        ));

        // Pâtisserie
        CATEGORY_KEYWORDS.put("Pâtisserie", Arrays.asList(
            "gâteau", "gateau", "tarte", "tartelette", "éclair", "eclair", "religieuse", "paris-brest",
            "millefeuille", "opéra", "opera", "fraisier", "forêt noire", "foret noire", "tiramisu",
            "macaron", "madeleine", "financier", "cannelé", "cannele", "flan pâtissier", "flan patissier",
            "chou", "profiterole", "saint-honoré", "saint-honore", "tropézienne", "tropezienne"
        ));

        // Épicerie Salée
        CATEGORY_KEYWORDS.put("Épicerie Salée", Arrays.asList(
            "sel", "poivre", "épice", "epice", "aromate", "bouillon", "cube", "soupe", "potage",
            "plat préparé", "plat prepare", "ravioli", "cassoulet", "choucroute", "couscous",
            "paella", "tajine", "curry", "nems", "samoussa", "pizza", "quiche", "tarte salée", "tarte salee"
        ));

        // Épicerie Sucrée
        CATEGORY_KEYWORDS.put("Épicerie Sucrée", Arrays.asList(
            "sucre", "cassonade", "miel", "confiture", "gelée", "gelee", "marmelade", "pâte à tartiner",
            "pate a tartiner", "nutella", "sirop", "caramel", "compote", "fruits au sirop",
            "crème de marrons", "creme de marrons", "lait concentré", "lait concentre"
        ));

        // Céréales et Féculents
        CATEGORY_KEYWORDS.put("Céréales et Féculents", Arrays.asList(
            "riz", "basmati", "thaï", "thai", "arborio", "risotto", "sushi", "complet", "sauvage",
            "quinoa", "boulgour", "couscous", "semoule", "blé", "ble", "orge", "avoine", "millet",
            "sarrasin", "épeautre", "epeautre", "kamut", "lentille", "pois chiche", "pois cassé",
            "haricot sec", "fève", "feve", "flageolet"
        ));

        // Pâtes et Riz (sous-catégorie)
        CATEGORY_KEYWORDS.put("Pâtes et Riz", Arrays.asList(
            "pâte", "pate", "spaghetti", "penne", "fusilli", "farfalle", "tagliatelle", "linguine",
            "macaroni", "coquillette", "nouille", "vermicelle", "lasagne", "cannelloni", "ravioli",
            "tortellini", "gnocchi"
        ));

        // Conserves
        CATEGORY_KEYWORDS.put("Conserves", Arrays.asList(
            "conserve", "boîte", "boite", "thon en boîte", "thon en boite", "sardine en boîte",
            "maquereau", "saumon en boîte", "maïs", "mais", "petits pois", "haricots verts",
            "flageolets", "cassoulet", "ravioli", "tomate pelée", "tomate pelee", "concentré de tomate",
            "concentre de tomate", "ratatouille", "macedoine", "macédoine"
        ));

        // Sauces et Condiments
        CATEGORY_KEYWORDS.put("Sauces et Condiments", Arrays.asList(
            "sauce", "ketchup", "mayonnaise", "moutarde", "cornichon", "pickle", "câpre", "capre",
            "olive", "tapenade", "pesto", "sauce tomate", "bolognaise", "carbonara", "pesto",
            "sauce soja", "sauce nuoc-mâm", "sauce nuoc-mam", "tabasco", "harissa", "wasabi",
            "sambal", "sriracha", "aïoli", "aioli", "rouille", "béarnaise", "bearnaise", "hollandaise"
        ));

        // Huiles et Vinaigres
        CATEGORY_KEYWORDS.put("Huiles et Vinaigres", Arrays.asList(
            "huile", "olive", "tournesol", "colza", "noix", "noisette", "sésame", "sesame",
            "arachide", "pépins de raisin", "pepins de raisin", "coco", "avocat", "lin",
            "vinaigre", "balsamique", "vin", "cidre", "framboise", "xérès", "xeres", "riz"
        ));

        // Boissons
        CATEGORY_KEYWORDS.put("Boissons", Arrays.asList(
            "eau", "eau minérale", "eau minerale", "eau gazeuse", "eau plate", "perrier", "badoit",
            "vittel", "evian", "contrex", "hépar", "hepar", "volvic",
            "jus", "jus d'orange", "jus de pomme", "nectar", "smoothie", "sirop",
            "soda", "coca", "pepsi", "sprite", "fanta", "orangina", "schweppes", "limonade",
            "thé", "the", "infusion", "tisane", "café", "cafe", "expresso", "cappuccino",
            "chocolat chaud", "lait chocolaté", "lait chocolate", "ice tea", "boisson énergisante",
            "boisson energisante", "red bull", "monster"
        ));

        // Boissons Alcoolisées
        CATEGORY_KEYWORDS.put("Boissons Alcoolisées", Arrays.asList(
            "vin", "rouge", "blanc", "rosé", "rose", "champagne", "prosecco", "crémant", "cremant",
            "bière", "biere", "blonde", "brune", "ambrée", "ambree", "blanche", "ipa", "stout",
            "whisky", "vodka", "gin", "rhum", "tequila", "cognac", "armagnac", "calvados",
            "pastis", "ricard", "liqueur", "porto", "apéritif", "aperitif", "martini", "vermouth"
        ));

        // Surgelés
        CATEGORY_KEYWORDS.put("Surgelés", Arrays.asList(
            "surgelé", "surgele", "congelé", "congele", "frozen", "frite", "légumes surgelés",
            "legumes surgeles", "poisson surgelé", "poisson surgele", "pizza surgelée", "pizza surgelee",
            "glace", "crème glacée", "creme glacee", "sorbet", "bâtonnet", "batonnet", "esquimau",
            "cornet", "pot de glace", "magnum", "ben & jerry"
        ));

        // Snacks et Apéritifs
        CATEGORY_KEYWORDS.put("Snacks et Apéritifs", Arrays.asList(
            "chips", "tuiles", "crackers", "biscuit apéritif", "biscuit aperitif", "bretzel", "pretzel",
            "cacahuète", "cacahuete", "pistache", "amande", "noix", "noisette", "noix de cajou",
            "mélange apéritif", "melange aperitif", "olives", "tapenade", "houmous", "guacamole",
            "tzatziki", "tarama", "rillettes", "terrine"
        ));

        // Confiserie et Chocolat
        CATEGORY_KEYWORDS.put("Confiserie et Chocolat", Arrays.asList(
            "chocolat", "tablette", "noir", "lait", "blanc", "praliné", "praline", "noisette",
            "bonbon", "dragée", "dragee", "sucette", "caramel", "toffee", "fudge", "nougat",
            "guimauve", "marshmallow", "réglisse", "reglisse", "menthe", "chewing-gum", "haribo",
            "m&ms", "smarties", "twix", "mars", "snickers", "bounty", "kinder", "ferrero"
        ));

        // Petit-Déjeuner
        CATEGORY_KEYWORDS.put("Petit-Déjeuner", Arrays.asList(
            "céréales", "cereales", "corn flakes", "muesli", "granola", "flocons d'avoine",
            "flocons d'avoine", "porridge", "special k", "fitness", "chocapic", "nesquik",
            "confiture", "miel", "pâte à tartiner", "pate a tartiner", "biscotte", "pain grillé",
            "pain grille", "brioche", "croissant", "pain au chocolat"
        ));

        // Biscuits et Gâteaux
        CATEGORY_KEYWORDS.put("Biscuits et Gâteaux", Arrays.asList(
            "biscuit", "cookie", "sablé", "sable", "petit-beurre", "petit-lu", "galette",
            "palet", "shortbread", "digestive", "prince", "bn", "petit écolier", "petit ecolier",
            "granola", "brownie", "muffin", "cupcake", "quatre-quarts", "quatre-quarts",
            "cake", "madeleines", "financiers", "speculoos", "oreo", "doré", "dore"
        ));

        // Produits Bio
        CATEGORY_KEYWORDS.put("Produits Bio", Arrays.asList(
            "bio", "biologique", "agriculture biologique", "ab", "organic", "nature",
            "équitable", "equitable", "commerce équitable", "commerce equitable"
        ));

        // Produits Sans Gluten
        CATEGORY_KEYWORDS.put("Produits Sans Gluten", Arrays.asList(
            "sans gluten", "gluten free", "cœliaque", "coeliaque"
        ));

        // Produits Végétariens
        CATEGORY_KEYWORDS.put("Produits Végétariens", Arrays.asList(
            "végétarien", "vegetarien", "végétalien", "vegetalien", "vegan", "tofu", "seitan",
            "tempeh", "protéine végétale", "proteine vegetale", "steak végétal", "steak vegetal",
            "galette végétale", "galette vegetale", "lait végétal", "lait vegetal", "lait d'amande",
            "lait de soja", "lait d'avoine", "lait de coco"
        ));

        // Produits pour Bébé
        CATEGORY_KEYWORDS.put("Produits pour Bébé", Arrays.asList(
            "bébé", "bebe", "baby", "nourrisson", "lait infantile", "lait maternisé", "lait maternise",
            "biberon", "tétine", "tetine", "petit pot", "purée bébé", "puree bebe", "compote bébé",
            "compote bebe", "couche", "lange", "lingette", "liniment", "crème change", "creme change"
        ));

        // Hygiène et Beauté
        CATEGORY_KEYWORDS.put("Hygiène et Beauté", Arrays.asList(
            "shampoing", "shampooing", "après-shampoing", "apres-shampoing", "gel douche",
            "savon", "dentifrice", "brosse à dents", "brosse a dents", "déodorant", "deodorant",
            "parfum", "eau de toilette", "crème", "creme", "lait corporel", "huile",
            "maquillage", "fond de teint", "mascara", "rouge à lèvres", "rouge a levres",
            "vernis", "dissolvant", "coton", "coton-tige", "papier toilette", "mouchoir",
            "serviette hygiénique", "serviette hygienique", "tampon", "protège-slip", "protege-slip",
            "rasoir", "mousse à raser", "mousse a raser", "after-shave"
        ));

        // Entretien de la Maison
        CATEGORY_KEYWORDS.put("Entretien de la Maison", Arrays.asList(
            "lessive", "adoucissant", "détachant", "detachant", "eau de javel", "javel",
            "nettoyant", "désinfectant", "desinfectant", "liquide vaisselle", "tablette lave-vaisselle",
            "sel régénérant", "sel regenerant", "liquide de rinçage", "liquide de rincage",
            "éponge", "eponge", "lavette", "chiffon", "serpillière", "serpilliere", "balai",
            "sac poubelle", "aluminium", "film étirable", "film etirable", "papier cuisson",
            "essuie-tout", "sopalin", "ajax", "cif", "mr propre", "ariel", "dash", "skip"
        ));

        // Animalerie
        CATEGORY_KEYWORDS.put("Animalerie", Arrays.asList(
            "chat", "chien", "animal", "croquette", "pâtée", "patee", "litière", "litiere",
            "friandise pour chat", "friandise pour chien", "jouet pour chat", "jouet pour chien",
            "laisse", "collier", "shampoing pour chien", "shampoing pour chat"
        ));

        // Papeterie
        CATEGORY_KEYWORDS.put("Papeterie", Arrays.asList(
            "cahier", "carnet", "feuille", "classeur", "chemise", "intercalaire",
            "stylo", "crayon", "gomme", "règle", "regle", "équerre", "equerre", "compas",
            "taille-crayon", "correcteur", "surligneur", "feutre", "marqueur", "colle",
            "scotch", "agrafe", "agrafeuse", "perforatrice", "ciseaux"
        ));

        // Jardinerie
        CATEGORY_KEYWORDS.put("Jardinerie", Arrays.asList(
            "graine", "semence", "plant", "bulbe", "terreau", "engrais", "compost",
            "pot", "jardinière", "jardiniere", "bac", "arrosoir", "tuyau", "pelle", "râteau", "rateau",
            "sécateur", "secateur", "tondeuse", "débroussailleuse", "debroussailleuse"
        ));

        // Bricolage
        CATEGORY_KEYWORDS.put("Bricolage", Arrays.asList(
            "vis", "clou", "écrou", "ecrou", "boulon", "cheville", "perceuse", "visseuse",
            "marteau", "tournevis", "clé", "cle", "pince", "scie", "niveau", "mètre", "metre",
            "ruban", "peinture", "pinceau", "rouleau", "bâche", "bache", "enduit", "plâtre", "platre"
        ));

        // Textile
        CATEGORY_KEYWORDS.put("Textile", Arrays.asList(
            "tee-shirt", "t-shirt", "chemise", "pantalon", "jean", "short", "jupe", "robe",
            "pull", "gilet", "veste", "manteau", "blouson", "imperméable", "impermeable",
            "chaussette", "collant", "sous-vêtement", "sous-vetement", "slip", "caleçon", "calecon",
            "soutien-gorge", "culotte", "pyjama", "chaussure", "basket", "sandal", "botte"
        ));
    }

    /**
     * Déduit la catégorie basée sur le nom du produit
     */
    public static String inferCategory(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "Autre";
        }

        String name = productName.toLowerCase().trim();

        // Parcourir toutes les catégories
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            // Vérifier si un des mots-clés est présent dans le nom du produit
            for (String keyword : keywords) {
                if (name.contains(keyword)) {
                    return category;
                }
            }
        }

        return "Autre";
    }

    /**
     * Obtient toutes les catégories
     */
    public static List<String> getAllCategories() {
        return new ArrayList<>(ALL_CATEGORIES);
    }
}

