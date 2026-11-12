# Framework de Automatización SauceDemo

Framework de automatización de pruebas para [SauceDemo](https://www.saucedemo.com/) construido con Selenium WebDriver, Java y TestNG, siguiendo el patrón de diseño Page Object Model (POM) con Page Factory.

## Descripción general

Este proyecto automatiza tres flujos de usuario principales de la aplicación web SauceDemo:

- **Flujo de compra**: checkout completo de extremo a extremo, desde el login hasta la confirmación del pedido, seleccionando un producto aleatorio.
- **Gestión del carrito**: agregar tres productos diferentes al carrito, eliminarlos y verificar que el carrito quede vacío.
- **Logout**: verificar que el usuario sea redirigido correctamente a la página de login después de cerrar sesión.

## Stack tecnológico

| Herramienta | Versión | Propósito |
|---|---|---|
| Java | 17 | Lenguaje de programación |
| Maven | 3.x | Compilación y gestión de dependencias |
| Selenium WebDriver | 4.47.0 | Automatización del navegador |
| TestNG | 7.10.2 | Ejecutor de pruebas y anotaciones |
| Chrome | Última | Navegador objetivo |

## Estructura del proyecto

```
SauceDemoAutomation/
├── .github/
│   └── workflows/
│       └── ci.yml                  # Pipeline CI (push/PR/cron diario) + publicación al dashboard
├── src/test/java/com/saucedemo/
│   ├── base/
│   │   ├── BaseTest.java       # Setup/teardown de WebDriver (@BeforeMethod/@AfterMethod)
│   │   └── BasePage.java       # Inicialización de Page Factory, lógica compartida de logout
│   ├── pages/
│   │   ├── LoginPage.java
│   │   ├── InventoryPage.java
│   │   ├── CartPage.java
│   │   ├── CheckoutStepOnePage.java
│   │   ├── CheckoutStepTwoPage.java
│   │   └── CheckoutCompletePage.java
│   ├── listeners/
│   │   └── ScreenshotListener.java  # Captura automática de pantallas en fallos
│   └── tests/
│       ├── PurchaseTest.java   # Usa @DataProvider para múltiples usuarios
│       ├── RemoveCartItemsTest.java
│       └── LogoutTest.java
├── src/test/resources/
│   └── allure.properties           # Dirige los resultados Allure a target/allure-results
├── allure-results/                 # Generados por el adapter Allure (no versionados)
├── screenshots/                    # Generadas automáticamente en fallos (no versionadas)
├── pom.xml
├── .gitignore
└── README.md
```

## Decisiones de diseño

- **Page Object Model con Page Factory**: cada página está representada por una clase que usa anotaciones `@FindBy`. Los localizadores viven exclusivamente en las clases de páginas; las clases de pruebas nunca interactúan directamente con `WebElement`.
- **Encadenamiento de métodos entre páginas**: cualquier método que dispara navegación devuelve el Page Object de la página siguiente (p. ej., `login()` devuelve `InventoryPage`), manteniendo las pruebas fluidas y desacopladas de los detalles de implementación.
- **Clases base compartidas**: `BaseTest` centraliza la gestión del ciclo de vida de WebDriver; `BasePage` centraliza la inicialización de Page Factory y la funcionalidad entre páginas (menú de logout, disponible desde cualquier página con sesión iniciada).
- **Esperas explícitas**: `BasePage` expone `waitForVisible()` sobre un `WebDriverWait` compartido (10 s), usado como punto de partida en las transiciones de página. En puntos puntuales del flujo de checkout donde se requieren condiciones distintas (p. ej., clicabilidad del botón *Finish* en `CheckoutStepTwoPage`) se crean `WebDriverWait` adicionales con tiempos propios.
- **Preferencias de Chrome**: la detección de fugas de contraseñas y el gestor de contraseñas se desactivan vía `ChromeOptions` para evitar que los diálogos nativos del navegador interfieran con la ejecución de las pruebas.
- **Configuración externalizada**: la URL base y el modo headless se resuelven con la prioridad `propiedad del sistema -D` → `variable de entorno` → `valor por defecto`, de modo que la suite corre en cualquier máquina (incluidos agentes de CI) sin cambios de código.

## Características avanzadas de TestNG

- **`@DataProvider`** en `PurchaseTest`: ejecuta el flujo de compra contra múltiples usuarios de SauceDemo (`standard_user`, `performance_glitch_user`) para validar el flujo con diferentes comportamientos de usuario simulados.
- **`ITestListener` personalizado** (`ScreenshotListener`): captura automáticamente una captura de pantalla cada vez que una prueba falla, guardada en el directorio `screenshots/` con un nombre de archivo con marca de tiempo para trazabilidad.

## Cómo ejecutar

Clona el repositorio y ejecuta:

```bash
mvn clean test
```

O, desde IntelliJ IDEA: clic derecho sobre el paquete `tests` → **Run 'Tests in com.saucedemo.tests'**.

### Prerrequisitos

- JDK 17 o superior
- Maven 3.6+
- Google Chrome instalado (ChromeDriver se resuelve automáticamente vía Selenium Manager)

### Opciones de configuración

| Parámetro | Propiedad del sistema | Variable de entorno | Por defecto |
|---|---|---|---|
| URL base | `-Dbase.url` | `BASE_URL` | `https://www.saucedemo.com/` |
| Modo headless | `-Dheadless=true` | `HEADLESS=true` | `false` |

Ejemplo con configuración personalizada:

```bash
mvn clean test -Dheadless=true -Dbase.url=https://www.saucedemo.com/
```

## Evidencia de pruebas

Una captura de pantalla que confirma la ejecución exitosa de todos los escenarios de prueba (`Tests run: 4, Failures: 0`) está disponible en `docs/evidencia/test-execution-success.png`.

## Pipeline CI/CD

La suite corre automáticamente en GitHub Actions en cada push/PR a `main`,
manualmente (workflow_dispatch) y **a diario a las 06:00 UTC (01:00 Colombia)**
vía cron. Cada ejecución produce:

| Artefacto | Descripción |
|---|---|
| `surefire-reports-web` | Reportes XML de Surefire |
| `allure-results-web` | Resultados crudos de Allure |
| `allure-report-web` | Reporte HTML navegable de Allure |

### Publicación automática al dashboard

Después de cada ejecución (incluso con fallos), el pipeline parsea los XML de
Surefire y publica los resultados consolidados en
[Proyecto-grado-dashboard](https://github.com/AngelicaJuliethSP/Proyecto-grado-dashboard),
que alimenta el dashboard público de QA:
**https://proyecto-grado-dashboard.streamlit.app**

Esto hace que el dashboard se actualice de forma **100 % automática** — sin
pasos manuales. La publicación usa un **bucle de reintento** (hasta 5
intentos con fetch + regeneración sobre el histórico remoto) para resolver
la carrera cuando los 3 repos publican a la vez. Requiere el secreto `DASHBOARD_TOKEN` (PAT fine-grained con
acceso de escritura al repo del dashboard). Si el secreto no existe, el paso
se omite de forma controlada y la ejecución no se ve afectada.

## Autora

Angélica Julieth Silva Pérez — College Trainee, Globant
