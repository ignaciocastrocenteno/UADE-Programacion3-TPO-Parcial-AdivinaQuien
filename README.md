# Trabajo Práctico Obligatorio (TPO) Parcial: "Adivina Quién"

## Documento de Cambios, Fundamentación Algorítmica y Decisiones de Diseño

**- Asignatura:** Programación III
**- Docentes:** Juan Ignacio López (titular) y Juan Pablo Dieguez (auxiliar)
**- Institución:** Universidad Argentina de la Empresa (UADE)
**- Lenguaje y Entorno:** Java SE Vanilla (17+) sin dependencias externas
**- Alumnos:** Agustín Acevedo, Hernán You, Sebastián De La Fuente e Ignacio Julián Castro Centeno.

---

## 1. Introducción y Alcance de la Evolución

El presente proyecto implementa la evolución integral del juego **"Adivina Quién"**, transitando desde un prototipo inicial de demostración (7 personajes, búsquedas estáticas y comodín rígido) hacia una arquitectura orientada a objetos desacoplada, extensible y analíticamente sólida basada en principios avanzados de **Algoritmos Voraces (Greedy)** y **División y Conquista (Divide & Conquer)**.

### Cuadro Comparativo: Prototipo Original vs. TPO Evolutivo

| Eje de Evaluación               | Prototipo Inicial (7 Personajes)                | Solución TPO Parcial (23 Personajes)                                                                                    |
| :------------------------------ | :---------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------- |
| **Población del Dominio**       | 7 personajes sin taxonomía física.              | **23 personajes** únicos con 5 dimensiones fenotípicas declaradas.                                                      |
| **Ordenamiento y Carga**        | Inserción sin orden formal; IDs manuales.       | **Orden inicial por género**; la máquina asigna IDs autoincrementales consecutivos **1..23**.                           |
| **Modelo del Personaje**        | Objeto anémico con flag mutable `esElegido`.    | Entidad inmutable (`Persona.java`) con Enums fuertemente tipados (`Genero`, `ColorPelo`).                               |
| **Encapsulamiento del Secreto** | Acceso público o directo a la variable secreta. | **Árbitro/Oráculo imparcial (`ArbitroJuego`)**: encapsulamiento estricto; aislamiento total entre competidores.         |
| **Mecánica de Partida**         | Búsqueda aislada sobre un único arreglo.        | **Juego competitivo por turnos**: Humano vs Máquina 1, Humano vs Máquina 2 y Máquina 1 vs Máquina 2.                    |
| **Inteligencias Artificiales**  | Métodos imperativos estáticos.                  | **Dos agentes autónomos polimórficos**: Máquina 1 (Greedy biseccional) y Máquina 2 (Asertiva con ventaja de historial). |
| **Paradigma Algorítmico**       | Recorrido lineal y agrupación fija de a 3.      | **Divide & Conquer** (partición de espacios) y **Greedy Algorithm** (maximización de ganancia de información).          |
| **Persistencia de Récords**     | No contemplada.                                 | **Persistencia en memoria** con ranking descendente y sincronización a JSON estándar en raíz.                           |
| **Observabilidad**              | Impresiones básicas por consola.                | Trazabilidad algorítmica paso a paso con explicación del razonamiento voraz en cada turno.                              |

---

## 2. Arquitectura de Paquetes y Responsabilidades

La solución sigue principios de alta cohesión y bajo acoplamiento, estructurada en 6 paquetes temáticos:

```
src/ar/edu/uade/pr3/ejercicios/ejercicio_adivina_quien/
├── Main.java                                   [Punto de entrada interactivo; consola con validación de entradas]
├── modelos/
│   ├── Genero.java                            [Enum: MASCULINO, FEMENINO]
│   ├── ColorPelo.java                         [Enum: COLORADO, NEGRO, AMARILLO, NINGUNO]
│   ├── Persona.java                           [Entidad inmutable del personaje con atributos tipados]
│   └── FiltroPregunta.java                    [Value Object que encapsula un predicado funcional java.util.function.Predicate]
├── algoritmos/
│   ├── FiltroParticion.java                   [Divide & Conquer: divide un conjunto en subconjuntos disjuntos y poda]
│   ├── SelectorPreguntaGreedy.java            [Algoritmo Voraz: evalúa el desbalance y elige la pregunta de corte óptimo]
│   └── HistorialPreguntas.java                [Registro sincronizado de auditoría y base de conocimiento para Máquina 2]
├── jugadores/
│   ├── Jugador.java                           [Clase base abstracta: gestiona candidatos restantes y deducciones]
│   ├── DecisionTurno.java                     [Value Object polimórfico: acción de preguntar o adivinar directamente]
│   ├── JugadorHumano.java                     [Especialización interactiva por consola]
│   ├── JugadorMaquina1.java                   [IA Greedy individual de corte óptimo]
│   └── JugadorMaquina2.java                   [IA Asertiva con asimilación del historial de Máquina 1]
├── juego/
│   ├── CatalogoPersonajes.java                [Fábrica central: construye los 23 personajes ordenados por género e ID]
│   ├── ArbitroJuego.java                      [Oráculo mediador; responde preguntas y valida secretos sin filtrarlos]
│   ├── PartidaAdivinaQuien.java               [Orquestador del ciclo de vida de la partida y simulador M1 vs M2]
│   ├── JuegoAdivinaQuien.java                 [Fachada (Facade) de alto nivel para retrocompatibilidad y acceso unificado]
│   └── RangoGrupo.java                        [Value Object para resultados del comodín de agrupación]
├── persistencia/
│   └── MarcadorRecord.java                    [Gestor de victorias en memoria (Map) con respaldo en marcador_record.json]
└── test/
    └── JuegoTest.java                         [Suite integral de 9 pruebas automatizadas]
```

---

## 3. Fundamentación de Algoritmos: Aplicados vs. No Aplicados

### 3.1. Divide & Conquer (Partición y Poda de Subespacios)

- **Implementación:** `FiltroParticion.java` y `Jugador.aplicarFiltro()`.
- **Mecánica algorítmica:**
  1. **Dividir:** Dado un conjunto de candidatos viables $C$ con cardinalidad $N = |C|$ y una consulta $p$, se particiona $C$ en dos subconjuntos disjuntos:
     $$C_{cumplen} = \{ c \in C \mid p(c) = \text{true} \}$$
     $$C_{noCumplen} = \{ c \in C \mid p(c) = \text{false} \}$$
     Cumpliéndose $C_{cumplen} \cap C_{noCumplen} = \emptyset$ y $C_{cumplen} \cup C_{noCumplen} = C$.
  2. **Conquistar / Podar:** El árbitro devuelve la respuesta booleana $R \in \{\text{true}, \text{false}\}$. El algoritmo descarta de inmediato el subconjunto complementario, reduciendo el espacio de búsqueda al subconjunto compatible:
     $$C_{nuevo} = \begin{cases} C_{cumplen} & \text{si } R = \text{true} \\ C_{noCumplen} & \text{si } R = \text{false} \end{cases}$$
- **Complejidad Temporal:** $O(N)$ para particionar mediante un único recorrido lineal sobre los candidatos actuales.
- **Ventaja frente a Búsqueda Lineal:** Una partición balanceada reduce el espacio de estados a razón de $N/2$ por turno, logrando convergencia en $O(\log_2 N)$ turnos (aproximadamente $\lceil \log_2 23 \rceil = 5$ turnos), mientras que la búsqueda lineal requiere $N/2 = 11.5$ turnos en promedio y hasta $23$ en el peor caso.

### 3.2. Algoritmo Voraz (Greedy Algorithm: Bisección Óptima)

- **Implementación:** `SelectorPreguntaGreedy.java`.
- **Mecánica algorítmica:**
  - En cada turno, la IA no sabe a priori si la respuesta del oponente será afirmativa o negativa. Para minimizar la incertidumbre en el peor escenario (principio Minimax), la IA evalúa todas las preguntas candidatas $p \in \mathcal{P}$ y calcula la métrica de desbalance:
    $$\Delta(p) = | |C_{cumplen}(p)| - |C_{noCumplen}(p)| |$$
  - **Función de Selección Voraz:** Selecciona el filtro $p^*$ que minimiza $\Delta(p)$:
    $$p^* = \arg\min_{p \in \mathcal{P}, |C_{cumplen}(p)| > 0, |C_{noCumplen}(p)| > 0} \Delta(p)$$
  - Si existen múltiples preguntas con el mismo desbalance mínimo, se prioriza aquella que se acerque más exactamente a la bisección perfecta ($50\% / 50\%$).
- **Relación con la Teoría de la Información (Entropía de Shannon):**
  Maximizar la bisección de candidatos equivale a maximizar la ganancia de información esperada:
  $$H(C) = - \sum_{i} P(c_i) \log_2 P(c_i)$$
  Una pregunta 50/50 entrega exactamente $1\text{ bit}$ de información pura, que es el límite teórico superior para una pregunta binaria.
- **Complejidad Temporal:** $O(|\mathcal{P}| \cdot N)$ por turno, donde $|\mathcal{P}| \le 10$ es el banco finito de filtros físicos y $N \le 23$. Dado que $|\mathcal{P}|$ y $N$ están acotados por constantes pequeñas, la evaluación voraz se ejecuta en microsegundos ($< 1\text{ ms}$).

### 3.3. Algoritmos Evaluados pero NO Aplicados

| Algoritmo No Aplicado                                        | Justificación Teórica de Rechazo                                                                                                                                                                                                                                                                                                                                                                                                          |
| :----------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Búsqueda Lineal Exhaustiva ($O(N)$)**                      | Rechazada como estrategia competitiva principal porque en un juego alternado por turnos, una complejidad lineal garantiza la derrota frente a cualquier oponente que reduzca el espacio logarítmicamente. Se preservó únicamente en `BusquedaLinealStrategy` por motivos didácticos y de compatibilidad con el enunciado original.                                                                                                        |
| **Backtracking (Vuelta Atrás)**                              | Inadecuado para este dominio. El backtracking requiere explorar ramas tentativas y deshacer decisiones (_undo/rollback_) al llegar a estados inválidos. En "Adivina Quién", las respuestas del árbitro son **verdades absolutas, deterministas y monótonas** sobre el secreto del oponente. Un candidato descartado jamás volverá a ser viable en turnos posteriores; por ende, no existe necesidad de retroceder en el árbol de estados. |
| **Programación Dinámica (Dynamic Programming)**              | Innecesaria. La programación dinámica exige subproblemas superpuestos y una función de valor óptimo que justifique el costo de memoización en tablas. En este juego, el espacio de estados $N \le 23$ se reduce tan rápidamente mediante la heurística Greedy que calcular recursivamente una tabla de subproblemas añadiría sobrecarga espacial y temporal sin aportar ventajas en la cantidad de turnos requeridos para ganar.          |
| **Búsqueda en Profundidad / Anchura (DFS / BFS Exhaustivo)** | Impráctico como generador de turnos. Construir el árbol completo de $2^{23}$ combinaciones posibles para una partida de 23 personajes generaría una explosión combinatoria innecesaria, cuando la bisección voraz local resuelve el problema en tiempo real con precisión óptima.                                                                                                                                                         |

---

## 4. Comparativa de Inteligencias Artificiales: Máquina 1 vs. Máquina 2

El enunciado evolutivo exige dos IAs con estilos de juego diferenciados, donde la **Máquina 2 sea más asertiva y parta con la ventaja de conocer las preguntas hechas por la Máquina 1**:

| Criterio                         | Máquina 1 (`JugadorMaquina1`)                                                 | Máquina 2 (`JugadorMaquina2`)                                                                                                                                                                   |
| :------------------------------- | :---------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ---- | --- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| **Filosofía de Juego**           | Conservadora, analítica y basada puramente en el estado de su propio tablero. | Asertiva, agresiva y capitalizadora de información externa.                                                                                                                                     |
| **Uso del Historial**            | Ninguno. Opera de forma aislada sin memoria de partidas o turnos de terceros. | **Asimila el historial compartido (`HistorialPreguntas`)**. Si detecta que Máquina 1 formuló una pregunta sobre el mismo objetivo, aplica la respuesta y poda candidatos sin consumir su turno. |
| **Umbral de Adivinanza Directa** | Exige **certeza matemática absoluta** ($                                      | C                                                                                                                                                                                               | = 1$ candidato restante) para arriesgar una suposición de ID. | Si $ | C   | \le 2$ candidatos, **arriesga una adivinanza directa** asumiendo un riesgo del $50\%$, lo que le permite ganar en un turno menos que Máquina 1. |
| **Trazabilidad**                 | Expone su cálculo de desbalance en cada turno.                                | Expone tanto su cálculo de corte como el informe de candidatos eliminados mediante la ventaja de historial.                                                                                     |

---

## 5. Fundamentación de Estructuras de Datos

### 5.1. `ArrayList<Persona>` vs. `LinkedList<Persona>`

- **Selección:** `ArrayList<Persona>`.
- **Fundamento:**
  - `ArrayList` ofrece acceso posicional directo en $O(1)$ gracias a su arreglo contiguo subyacente.
  - Presenta una **localidad de referencia espacial (cache-friendly)** insuperable frente a `LinkedList`, minimizando los _cache misses_ en los ciclos de iteración sobre candidatos.
  - `LinkedList` requiere instanciar nodos con dos punteros (`prev` y `next`), cuadruplicando el consumo de memoria por elemento y penalizando la iteración con saltos dispersos en la memoria Heap.

### 5.2. `HashMap<Integer, Persona>` para el Catálogo de Personajes

- **Selección:** `HashMap<Integer, Persona>` en `JuegoAdivinaQuien`.
- **Fundamento:**
  - El usuario y las IAs seleccionan y consultan personajes frecuentemente por su identificador numérico (`ID`).
  - `HashMap` provee recuperación directa en tiempo constante amortizado **$O(1)$**, a diferencia de recorrer una lista que demandaría $O(N)$.

### 5.3. `HashSet<FiltroPregunta>` para Prevención de Redundancia

- **Selección:** `HashSet` en `Jugador` y `JugadorMaquina2`.
- **Fundamento:**
  - En cada turno, la IA debe descartar las preguntas ya formuladas para no repetir consultas estériles.
  - La verificación de pertenencia `contains()` en un `HashSet` se resuelve en **$O(1)$** en promedio mediante la función hash del objeto `FiltroPregunta`, superando ampliamente el costo $O(P)$ de buscar en listas desordenadas.

### 5.4. Persistencia en Memoria con Respaldo JSON Opcional

- **Requerimiento del Usuario:** _"La persistencia debe ser obligatoriamente en memoria para la resolución de este proyecto, evitando necesitar una base de datos. En todo caso, como máximo crear un archivo JSON para almacenar los valores que sean necesarios, dispuesto a nivel raíz"_.
- **Implementación (`MarcadorRecord.java`):**
  - Mantiene un `Map<String, Integer> victoriasPorUsuario` en memoria volátil para inserción y lectura en $O(1)$.
  - Incorpora un mecanismo de sincronización que serializa y deserializa las entradas hacia/desde `marcador_record.json` en la raíz del proyecto.
  - La serialización se programó de forma nativa utilizando `BufferedReader` y `BufferedWriter`, sin requerir librerías externas de terceros (como Jackson o Gson), preservando la pureza y portabilidad de Java Vanilla.

---

## 6. Patrones de Diseño de Software Aplicados

1. **Patrón Mediator / Oráculo (`ArbitroJuego`)**:
   - Resuelve el requerimiento de encapsulamiento: ni el humano ni las máquinas pueden acceder a la variable del personaje secreto del oponente. El árbitro actúa como mediador neutral que recibe consultas, evalúa la condición contra el secreto y devuelve únicamente un valor booleano.
2. **Patrón Strategy (`EstrategiaResolucion`)**:
   - Desacopla la lógica de resolución algorítmica (`BusquedaLinealStrategy`, `BusquedaPorAgrupacionStrategy`) de la clase consumidora `JuegoAdivinaQuien`, permitiendo intercambiar estrategias en tiempo de ejecución.
3. **Patrón Facade (`JuegoAdivinaQuien`)**:
   - Ofrece una API limpia y unificada para crear partidas en cualquiera de sus modos (Humano vs M1, Humano vs M2, M1 vs M2), inicializar el catálogo de 23 personajes y consultar comodines de rango sin exponer la complejidad interna de las clases subyacentes.
4. **Patrón Singleton (`MarcadorRecord`)**:
   - Centraliza el acceso al marcador récord a través de `MarcadorRecord.getInstancia()`, asegurando que todas las partidas disputadas actualicen el mismo estado en memoria.
5. **Patrón Factory Method (`CatalogoPersonajes` y `FiltroPregunta`)**:
   - `CatalogoPersonajes.crearCatalogoOficial()` garantiza la construcción reproducible de los 23 personajes ordenados por género con IDs correlativos del 1 al 23.
   - `FiltroPregunta` expone métodos de fábrica estáticos (`esMasculino()`, `esCalvo()`, `usaLentes()`, `peloColorado()`, `rangoIds()`) que asocian el tipo de filtro con su predicado funcional correspondiente.

---

## 7. Tabla Resumen de Complejidades Asintóticas (Big-O)

| Operación / Componente                | Algoritmo / Estructura               | Mejor Caso |             Caso Promedio             |       Peor Caso       |  Complejidad Espacial   |
| :------------------------------------ | :----------------------------------- | :--------: | :-----------------------------------: | :-------------------: | :---------------------: | ----------- | --------- | --- | ----------- | --------- | --- | ----------- | --- |
| **Búsqueda por ID**                   | `HashMap.get(id)`                    |   $O(1)$   |                $O(1)$                 | $O(N)$ _(colisiones)_ |         $O(N)$          |
| **Partición y Poda**                  | `FiltroParticion` (Divide & Conquer) |   $O(N)$   |                $O(N)$                 |        $O(N)$         | $O(N)$ _(copia podada)_ |
| **Selección de Pregunta**             | `SelectorPreguntaGreedy`             |    $O(     |              \mathcal{P}              |       \cdot N)$       |           $O(           | \mathcal{P} | \cdot N)$ | $O( | \mathcal{P} | \cdot N)$ | $O( | \mathcal{P} | )$  |
| **Verificación de Pregunta Repetida** | `HashSet.contains()`                 |   $O(1)$   |                $O(1)$                 | $O(P)$ _(colisiones)_ |         $O(P)$          |
| **Búsqueda Lineal**                   | `BusquedaLinealStrategy`             |   $O(1)$   |               $O(N/2)$                |        $O(N)$         |         $O(1)$          |
| **Búsqueda por Agrupación**           | `BusquedaPorAgrupacionStrategy`      |   $O(K)$   |                $O(N)$                 |        $O(N)$         |         $O(K)$          |
| **Turnos hasta Ganar**                | Greedy + Divide & Conquer            | $1$ turno  | $\approx \lceil \log_2 23 \rceil = 5$ |      $6$ turnos       |         $O(N)$          |
| **Actualización de Récord**           | `MarcadorRecord` (Memoria)           |   $O(1)$   |                $O(1)$                 |        $O(1)$         |   $O(U)$ _(usuarios)_   |
| **Generación de Ranking**             | `Collections.sort()` (Timsort)       |   $O(U)$   |             $O(U \log U)$             |     $O(U \log U)$     |         $O(U)$          |

---

## 8. Guía de Validación y Suite de Pruebas

El comportamiento del sistema está validado mediante una batería exhaustiva de **9 pruebas automatizadas** en `JuegoTest.java`:

1. `testCatalogo23Personajes()`: Valida los 23 personajes, IDs correlativos 1..23, orden inicial por género y consistencia de atributos (ej. calvos con color `NINGUNO`).
2. `testDivideAndConquerParticion()`: Valida la división disjunta y la poda de subconjuntos sin pérdida de elementos.
3. `testAlgoritmoVorazGreedy()`: Demuestra formalmente que el selector voraz elige la pregunta con menor desbalance ($\Delta = 1$ en la primera ronda sobre 23 personajes).
4. `testVentajaAsimilacionMaquina2()`: Valida que Máquina 2 descarta candidatos del historial compartido sin gastar sus propios turnos.
5. `testComodinGruposRango()`: Verifica que el comodín dicotómico contenga al personaje secreto en el 100% de los casos.
6. `testBusquedaLinealEvolutiva()`: Confirma retrocompatibilidad de la búsqueda secuencial sobre los 23 personajes.
7. `testBusquedaPorAgrupacionEvolutiva()`: Confirma retrocompatibilidad de la búsqueda por bloques sobre los 23 personajes.
8. `testPersistenciaMarcadorMemoria()`: Verifica el acumulador de victorias y la ordenación correcta del ranking.
9. `testSimulacionPartidasCompletas()`: Ejecuta 10 partidas consecutivas de Máquina 1 vs Máquina 2, asegurando convergencia determinista en $\le 15$ turnos sin excepciones ni bucles infinitos.

### Comandos de Compilación y Ejecución:

```powershell
# 1. Compilar todo el proyecto
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# 2. Ejecutar Suite de Pruebas (9/9 OK)
java -cp out ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.test.JuegoTest

# 3. Iniciar Juego Interactivo por Consola
java -cp out ar.edu.uade.pr3.ejercicios.ejercicio_adivina_quien.Main
```
