//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@RestController
//@RequestMapping("/api/v1/users/{userId}/todos")
//public class TodoController {
//    @GetMapping()
//    public ResponseEntity<List<TodoResponse>> listar(
//            @PathVariable Long userId) {
//        return null;
//    }
//
//    @GetMapping("/{todoId}")
//    public ResponseEntity<TodoResponse> buscarPorId(
//            @PathVariable Long userId,
//            @PathVariable Long todoId) {
//        return null;
//    }
//
//    @PostMapping
//    public ResponseEntity<TodoResponse> criar(
//            @PathVariable Long userId,
//            @RequestBody TodoCreate dto,
//            UriComponentsBuilder uriBuilder) {
//        return null;
//    }
//
//    @PutMapping("/{todoId}")
//    public ResponseEntity<TodoResponse> atualizar(
//            @PathVariable Long userId,
//            @PathVariable Long todoId,
//            @RequestBody TodoUpdate dto) {
//        return null;
//    }
//
//    @PatchMapping("/{todoId}")
//    public ResponseEntity<TodoResponse> alterarParcial(
//            @PathVariable Long userId,
//            @PathVariable Long todoId,
//            @RequestBody TodoPatch dto) {
//        return null;
//    }
//
//    @DeleteMapping("/{todoId}")
//    public ResponseEntity<Void> remover(
//            @PathVariable Long userId,
//            @PathVariable Long todoId) {
//        return null;
//    }
//}