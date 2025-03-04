using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;
using System.Collections.Generic;

namespace ApiTestAutomation.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PetController : ControllerBase
    {
        private static readonly List<Pet> Pets = new List<Pet>
        {
            new Pet { Id = 1, Name = "Buddy", Status = "available" },
            new Pet { Id = 2, Name = "Max", Status = "sold" }
        };

        [HttpGet("{id}")]
        [SwaggerOperation(Summary = "Get a pet by ID")]
        [SwaggerResponse(200, "Success", typeof(Pet))]
        [SwaggerResponse(404, "Pet not found")]
        public IActionResult GetPet(int id)
        {
            var pet = Pets.Find(p => p.Id == id);
            if (pet == null)
                return NotFound();

            return Ok(pet);
        }

        [HttpPost]
        [SwaggerOperation(Summary = "Add a new pet")]
        [SwaggerResponse(201, "Pet created", typeof(Pet))]
        public IActionResult CreatePet([FromBody] Pet pet)
        {
            Pets.Add(pet);
            return CreatedAtAction(nameof(GetPet), new { id = pet.Id }, pet);
        }

        [HttpDelete("{id}")]
        [SwaggerOperation(Summary = "Delete a pet by ID")]
        [SwaggerResponse(200, "Pet deleted")]
        [SwaggerResponse(404, "Pet not found")]
        public IActionResult DeletePet(int id)
        {
            var pet = Pets.Find(p => p.Id == id);
            if (pet == null)
                return NotFound();

            Pets.Remove(pet);
            return Ok();
        }
    }

    public class Pet
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Status { get; set; }
    }
}
