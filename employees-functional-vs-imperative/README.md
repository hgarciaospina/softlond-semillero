# Employees Functional vs Imperative

## 📘 Descripción General

Este proyecto demuestra dos paradigmas de programación aplicados al mismo dominio empresarial: la gestión de empleados.  
Se implementan dos enfoques distintos:

1. **Programación Imperativa:**  
   Basada en instrucciones paso a paso, uso de bucles, variables mutables y control explícito del flujo.
2. **Programación Funcional:**  
   Basada en operaciones inmutables, uso de funciones puras, expresiones lambda, streams y composición.

El propósito principal es **comparar la legibilidad, mantenibilidad y expresividad** entre ambos estilos de programación.

---

## ⚙️ Servicios y Métodos

### 🔹 `getTotalSalaryFunctional()` y `getTotalSalaryImperative()`

**Funcional:** Usa `stream().mapToDouble(Employee::getSalary).sum()` para sumar los salarios de forma declarativa.  
**Imperativo:** Usa un bucle `for` acumulando manualmente el total en una variable `double total`.

---

### 🔹 `getEmployeesByDepartmentFunctional(UUID departmentId)` y `getEmployeesByDepartmentImperative(UUID departmentId)`

**Funcional:** Filtra con `stream().filter()` los empleados cuyo `department.getId()` coincide con el `UUID` recibido.  
**Imperativo:** Recorre la lista completa con un `for` y agrega manualmente a una lista auxiliar los que cumplan la condición.

---

### 🔹 `getNamesAboveAverageSalaryFunctional()` y `getNamesAboveAverageSalaryImperative()`

**Funcional:**  
1. Calcula el salario promedio con `mapToDouble(Employee::getSalary).average()`.  
2. Filtra los empleados con salario mayor al promedio.  
3. Devuelve la lista de nombres ordenados.

**Imperativo:**  
1. Calcula el total y el promedio con bucles.  
2. Usa un `for` para agregar los empleados que superen el promedio.  
3. Ordena la lista con `Collections.sort()`.

---

### 🔹 `getManagersFunctional()` y `getManagersImperative()`

**Funcional:** Usa `filter(Employee::isManager)` para retornar todos los empleados con `isManager = true`.  
**Imperativo:** Usa un bucle `for` verificando el atributo y agregando a la lista resultante manualmente.

---

### 🔹 `getEmployeesByManagerFunctional(UUID managerId)` y `getEmployeesByManagerImperative(UUID managerId)`

**Funcional:**  
Usa `stream().filter(e -> e.getManagerId().equals(managerId))` para obtener los subordinados de un jefe.

**Imperativo:**  
Itera con un `for` y compara cada `managerId` con el argumento recibido, agregando coincidencias a la lista de resultados.

---

### 🔹 `getOrderedListFunctional()` y `getOrderedListImperative()`

**Funcional:**  
Define un `Comparator` en cadena (`thenComparing`) y usa `stream().sorted(comp)` para ordenar por departamento, apellido y nombre.  
Cada elemento se transforma con `map()` para incluir el nombre del jefe.

**Imperativo:**  
1. Crea un `Comparator` similar.  
2. Ordena la lista con `Collections.sort()`.  
3. Usa un bucle `for` para formatear los resultados e incluir el nombre del jefe.

---

## 🧩 Comparativa: Paradigma Imperativo vs Funcional

| Criterio | Imperativo | Funcional |
|-----------|-------------|------------|
| **Estilo** | Paso a paso | Declarativo |
| **Mutabilidad** | Mutable | Inmutable |
| **Legibilidad** | Verboso | Conciso |
| **Mantenibilidad** | Media | Alta |
| **Uso de Streams** | No | Sí |
| **Uso de Lambda** | No | Sí |

---

## 🧠 Conclusión

El paradigma funcional reduce código repetitivo y mejora la legibilidad,  
mientras que el imperativo ofrece control explícito y familiaridad para principiantes.  
Ambos estilos coexisten en entornos empresariales modernos.

---

## 📄 Autor

**Henry García Ospina**  
👨‍💻 Ingeniero de Sistemas | Desarrollador Java / .NET  
📧 henrygarciaospina@gmail.com  
🔗 [GitHub](https://github.com/hgarciaospina) | [LinkedIn](https://www.linkedin.com/in/henry-garcia-ospina-219a2b1b2/)
