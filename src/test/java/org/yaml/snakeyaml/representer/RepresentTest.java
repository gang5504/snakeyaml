package org.yaml.snakeyaml.representer;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

public class RepresentTest extends TestCase {

    public void testCustomRepresenter() {
        Dumper dumper = new Dumper(new MyRepresenter(), new DumperOptions());
        Loader loader = new Loader(new MyConstructor());
        Yaml yaml = new Yaml(loader, dumper);
        String output = yaml.dump(new CustomBean("A", 1));
        assertEquals("!!Dice 'Ad1'\n", output);
        CustomBean bean = (CustomBean) yaml.load(output);
        assertEquals("A", bean.getPrefix());
        assertEquals(1, bean.getSuffix());
    }

    class CustomBean {
        private String prefix;
        private int suffix;

        public CustomBean(String prefix, int suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        public int getSuffix() {
            return suffix;
        }
    }

    class MyRepresenter extends Representer {
        public MyRepresenter() {
            this.representers.put(CustomBean.class, new RepresentDice());
        }

        private class RepresentDice implements Represent {
            public Node representData(Object data) {
                CustomBean coin = (CustomBean) data;
                String value = coin.getPrefix() + "d" + coin.getSuffix();
                return representScalar("!!Dice", value);
            }
        }
    }

    class MyConstructor extends Constructor {

        public MyConstructor() {
            this.yamlConstructors.put("tag:yaml.org,2002:Dice", new ConstuctDice());
        }

        private class ConstuctDice implements Construct {
            public Object construct(Node node) {
                String val = (String) constructScalar(node);
                return new CustomBean(val.substring(0, 1), Integer.parseInt(val.substring(2)));
            }
        }
    }
}
