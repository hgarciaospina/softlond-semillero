
# 🧠 Programación Funcional en Java — Proyecto Empleados

## 📘 Introducción
Este documento explica **paso a paso** la implementación práctica de los **principios de programación funcional en Java**, aplicados al análisis de empleados de una empresa.  
Cada punto corresponde a un reto que pone en práctica las clases teóricas sobre **funciones puras**, **inmutabilidad** e **interfaces funcionales** (`Predicate`, `Function`, `Consumer`, `Supplier`, etc.).

---

## 🧩 Estructura Base del Proyecto

### 📄 Clase `Empleado`
```java
public record Empleado(String nombre, String area, double salario, int aniosDeServicio) {
    public Empleado withArea(String nuevaArea) {
        return new Empleado(this.nombre, nuevaArea, this.salario, this.aniosDeServicio);
    }
    public Empleado withSalario(double nuevoSalario) {
        return new Empleado(this.nombre, this.area, nuevoSalario, this.aniosDeServicio);
    }
}
```

### 🗂️ Clase `EmpleadoRepository`
```java
public class EmpleadoRepository {
    public List<Empleado> getAllEmployees() {
        return List.of(
            new Empleado("Ana", "TI", 75000, 5),
            new Empleado("Beto", "Marketing", 60000, 3),
            new Empleado("Carlos", "TI", 90000, 8),
            new Empleado("Diana", "Ventas", 55000, 2),
            new Empleado("Elena", "Marketing", 80000, 7),
            new Empleado("Fernando", "Ventas", 58000, 4),
            new Empleado("Gaby", "TI", 120000, 10)
        );
    }
}
```

---

## ⚙️ Interfaces Funcionales de Java

| Interfaz | Propósito | Método Abstracto | Ejemplo de Uso |
|-----------|------------|------------------|----------------|
| `Predicate<T>` | Evaluar condiciones (retorna `boolean`) | `boolean test(T t)` | Filtrado de empleados |
| `Function<T, R>` | Transformar datos | `R apply(T t)` | Mapear nombre o salario |
| `Consumer<T>` | Ejecutar acción sin retorno | `void accept(T t)` | Envío de notificación |
| `Supplier<T>` | Generar valores | `T get()` | Crear objetos por defecto |
| `BiFunction<T, U, R>` | Combinar dos entradas | `R apply(T t, U u)` | Calcular métricas |
| `UnaryOperator<T>` | Transformar sin cambiar tipo | `T apply(T t)` | Aumentar salario |
| `BinaryOperator<T>` | Reducir o combinar | `T apply(T t, T u)` | Combinar resultados |

---

## 🚀 Desafíos Funcionales y Soluciones

### 1️⃣ Análisis de Paradigmas — Imperativo vs Declarativo
**Pregunta:** Diferencia entre ambos enfoques para calcular el salario promedio del área de TI.

**Imperativo:**
``` java
double total = 0;
int count = 0;
for (Empleado e : empleados) {
    if (e.area().equals("TI")) {
        total += e.salario();
        count++;
    }
}
double promedio = total / count;
```

**Declarativo:**
``` java
double promedio = empleados.stream()
    .filter(e -> e.area().equals("TI"))
    .mapToDouble(Empleado::salario)
    .average()
    .orElse(0);
```
✅ **Criterio:** Se usa `Stream` con `filter` y `mapToDouble` (declarativo).  
📤 **Salida:** Promedio de salario en TI = `95000.0`

---

### 2️⃣ Regla de Negocio — Determinar si es Senior
**Interfaz:** `Predicate<Empleado>`  
``` java
Predicate<Empleado> esSenior = e -> e.aniosDeServicio() > 5;
System.out.println("Ana es senior: " + esSenior.test(empleados.get(0)));
System.out.println("Diana es senior: " + esSenior.test(empleados.get(3)));
```
✅ **Criterio:** `Predicate` es ideal para evaluaciones booleanas.  
📤 **Salida:**
```
Ana es senior: false
Diana es senior: false
```

---

### 3️⃣ Transformación de Datos — Nombre en Mayúsculas
**Interfaz:** `Function<Empleado, String>`  
``` java
Function<Empleado, String> nombreMayusculas = e -> e.nombre().toUpperCase();
System.out.println(nombreMayusculas.apply(empleados.get(1)));
```
✅ **Criterio:** `Function` transforma un tipo `Empleado` en `String`.  
📤 **Salida:** `BETO`

---

### 4️⃣ Acción sobre Datos — Envío de Correo
**Interfaz:** `Consumer<Empleado>`  
``` java
Consumer<Empleado> enviarCorreo = e ->
    System.out.println("Feliz aniversario, " + e.nombre() + "!");
enviarCorreo.accept(empleados.get(2));
```
✅ **Criterio:** `Consumer` ejecuta una acción sin retorno.  
📤 **Salida:** `Feliz aniversario, Carlos!`

---

### 5️⃣ Generador de Datos — Crear Empleado Becario
**Interfaz:** `Supplier<Empleado>`  
``` java
Supplier<Empleado> crearBecario = () -> new Empleado("Becario", "N/A", 15000, 0);
System.out.println(crearBecario.get());
```
✅ **Criterio:** `Supplier` genera datos sin parámetros.  
📤 **Salida:** `Empleado[nombre=Becario, area=N/A, salario=15000.0, aniosDeServicio=0]`

---

### 6️⃣ Contrato Funcional Personalizado — Calcular Bono
**Definición personalizada:**
``` java
@FunctionalInterface
interface CalculadoraBono {
    double calcular(Empleado e);
}

CalculadoraBono bonoAnual = e -> e.salario() * 0.1 * e.aniosDeServicio();
System.out.println("Bono de Gaby: $" + bonoAnual.calcular(empleados.get(6)));
```
✅ **Criterio:** Interfaz funcional a medida con un solo método `calcular`.  
📤 **Salida:** `Bono de Gaby: $120000.0`

---

### 7️⃣ Combinación de Reglas
**Interfaz:** `Predicate<Empleado>` con composición `.and()`  
``` java
Predicate<Empleado> esMarketing = e -> e.area().equals("Marketing");
Predicate<Empleado> salarioAlto = e -> e.salario() > 70000;
Predicate<Empleado> reglaCombinada = esMarketing.and(salarioAlto);

System.out.println("Elena cumple: " + reglaCombinada.test(empleados.get(4)));
System.out.println("Beto cumple: " + reglaCombinada.test(empleados.get(1)));
```
✅ **Criterio:** Uso de composición lógica `and()`.  
📤 **Salida:**
```
Elena cumple: true
Beto cumple: false
```

---

### 8️⃣ Composición de Transformaciones
**Interfaz:** `Function<T, R>` con `.andThen()`  
``` java
Function<Empleado, Double> aplicarAumento = e -> e.salario() * 1.10;
Function<Double, String> formatear = s -> "Nuevo Salario: $" + s;
Function<Empleado, String> operacionCompuesta = aplicarAumento.andThen(formatear);
System.out.println(operacionCompuesta.apply(empleados.get(5)));
```
✅ **Criterio:** Encadenamiento de funciones (`Function` → `andThen`).  
📤 **Salida:** `Nuevo Salario: $63800.0`

---

### 9️⃣ Pureza Funcional
**Función pura (sin mutar el original):**
``` java
public static double calcularProximoAumentoPuro(Empleado e, double porcentaje) {
    return e.salario() * (1 + porcentaje / 100);
}
```
✅ **Criterio:** Sin efectos secundarios, ni mutación de estado.  
📤 **Salida:**
```
Salario original: 120000.0
Salario calculado: 138000.0
```

---

### 🔟 Inmutabilidad con Método “Wither”
**Uso del record `Empleado`:**
``` java
Empleado ana = new Empleado("Ana", "TI", 75000, 5);
Empleado anaPromovida = ana.withArea("Gerencia de TI").withSalario(95000);
System.out.println("Original: " + ana);
System.out.println("Promovida: " + anaPromovida);
```
✅ **Criterio:** Se crea nueva instancia (inmutabilidad).  
📤 **Salida:**
```
Original: Empleado[nombre=Ana, area=TI, salario=75000.0, aniosDeServicio=5]
Promovida: Empleado[nombre=Ana, area=Gerencia de TI, salario=95000.0, aniosDeServicio=5]
```

---

## 🧾 Conclusiones
- Se aplicaron **todas las interfaces funcionales estándar**.
- El código demuestra **pureza**, **inmutabilidad** y **declaratividad**.
- Cada solución está orientada al uso de **contratos funcionales reutilizables**.
- El diseño final permite mantener un código **limpio, predecible y escalable**.

---
**Autor:** Henry García Ospina  
📍 Dosquebradas, Risaralda — Colombia  
📧 henrygarciaospina@gmail.com  
🔗 [LinkedIn](https://www.linkedin.com/in/henry-garcia-ospina-219a2b1b2)
