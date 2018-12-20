package functions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;



//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
//@AutoConfigureMockMvc
public class HandlerTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	Application function;

	//@Test
	public void testApply() throws Exception {
		FileSystemResourceLoader r = new FileSystemResourceLoader();
		byte[] json = StreamUtils.copyToByteArray(r.getResource("event.json").getInputStream());
		mvc.perform(post("/function").content(json)).andExpect(status().isOk());
	}

}
