package absolutelyaya.goop.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Calculatable(String formula)
{
	public static final Codec<Calculatable> CODEC = Codec.either(Codec.stringResolver(Calculatable::formula, Calculatable::new),
					Codec.FLOAT.xmap(i -> new Calculatable(String.valueOf(i)), i -> i.calculate(Map.of()))).xmap(Either::unwrap, Either::left);
	
	public float calculate(Map<String, Float> vars)
	{
		String curFormula = formula.replace(" ", "");
		for (Map.Entry<String, Float> var : vars.entrySet())
			curFormula = curFormula.replace(var.getKey(), var.getValue().toString());
		curFormula = Func.resolveFunctions(curFormula);
		curFormula = resolveBrackets(curFormula);
		final Pattern pattern = Pattern.compile("[A-Za-z]+");
		final Matcher matcher = pattern.matcher(curFormula);
		while(matcher.find())
			curFormula = curFormula.replace(matcher.group(), "0");
		return eval(curFormula);
	}
	
	String resolveBrackets(String equation)
	{
		Stack<Integer> open = new Stack<>();
		for (int i = 0; i < equation.length(); i++)
		{
			if(equation.charAt(i) == '(')
				open.push(i + 1);
			else if(!open.isEmpty() && equation.charAt(i) == ')')
			{
				String content = equation.substring(open.peek(), i);
				String resolved = resolveBrackets(content);
				equation = equation.replace("(" + content + ")", resolved);
				i = open.pop();
			}
		}
		return equation;
	}
	
	float eval(String equation)
	{
		while(true)
		{
			String[] segments =  equation.split("((?=[+\\-/*])|(?<=[+\\-/*]))");
			if(segments.length == 1)
				break;
			boolean skipAddition = false;
			for (int i = 0; i < segments.length - 1; i++)
			{
				if(segments[i + 1].equals("*"))
				{
					equation = equation.replace(segments[i] + "*" + segments[i + 2],
							String.valueOf(Float.parseFloat(segments[i]) * Float.parseFloat(segments[i + 2])));
					skipAddition = true;
					break;
				}
				else if(segments[i + 1].equals("/"))
				{
					equation = equation.replace(segments[i] + "/" + segments[i + 2],
							String.valueOf(Float.parseFloat(segments[i]) / Float.parseFloat(segments[i + 2])));
					skipAddition = true;
					break;
				}
			}
			if(!skipAddition)
			{
				float result = Float.parseFloat(segments[0]);
				for (int i = 0; i < segments.length - 1; i++)
				{
					if(segments[i + 1].equals("+"))
					{
						result += Float.parseFloat(segments[i + 2]);
						i += 1;
					}
					else if(segments[i + 1].equals("-"))
					{
						result -= Float.parseFloat(segments[i + 2]);
						i += 1;
					}
				}
				return result;
			}
		}
		if(equation.isEmpty())
			return 0f;
		return Float.parseFloat(equation);
	}
	
	public record Range(Calculatable min, Calculatable max)
	{
		public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Calculatable.CODEC.fieldOf("min").forGetter(Range::min),
				Calculatable.CODEC.fieldOf("max").forGetter(Range::max)
		).apply(instance, Range::new));
		
		public Vector2f calculateRange(Map<String, Float> vars)
		{
			return new Vector2f(min.calculate(vars), max.calculate(vars));
		}
		
		public float getRandomValue(Map<String, Float> vars, Random random)
		{
			Vector2f range = calculateRange(vars);
			return range.x + random.nextFloat() * (range.y - range.x);
		}
	}
	
	public record Color(Calculatable r, Calculatable g, Calculatable b, Calculatable a) implements IColor
	{
		public static final Codec<Color> CODEC =
				Calculatable.CODEC.listOf(3, 4).comapFlatMap(Color::map, color -> List.of(color.r, color.g, color.b, color.a));
		
		static DataResult<Color> map(List<Calculatable> list)
		{
			if(list.size() >= 3)
				return DataResult.success(new Color(list.get(0), list.get(1), list.get(2), list.size() >= 4 ? list.get(3) : new Calculatable("1")));
			return DataResult.error(() -> "Input is not a List of 3 or 4 Elements.");
		}
		
		public int getColor(Map<String, Float> vars)
		{
			return new java.awt.Color(r.calculate(vars), g.calculate(vars), b.calculate(vars), a.calculate(vars)).getRGB();
		}
	}
	
	public record Func(String id, Function<Float[], Float> processor)
	{
		static final Random rand = Random.create();
		
		static final Func MAX = new Func("max", i -> Math.max(i[0], i[1]));
		static final Func MIN = new Func("min", i -> Math.min(i[0], i[1]));
		static final Func CLAMP = new Func("clamp", i -> Math.clamp(i[0], i[1], i[2]));
		static final Func RANDOM_FLOAT = new Func("rand", i -> {
			if(i.length >= 2)
				return i[0] + rand.nextFloat() * (i[1] - i[0]);
			else if(i.length == 1)
				return rand.nextFloat() * i[0];
			else
				return rand.nextFloat();
		});
		static final Func RANDOM_BOOL = new Func("randb", i -> {
			if(i.length > 0)
				return rand.nextFloat() > i[0] ? 1f : 0f;
			else
				return rand.nextFloat() > 0.5 ? 1f : 0f;
		});
		static final Func POWER = new Func("pow", i -> (float)Math.pow(i[0], i[1]));
		
		public static final Func[] ALL_FUNCS = new Func[] {MAX, MIN, CLAMP, RANDOM_FLOAT, RANDOM_BOOL, POWER};
		
		public static String resolveFunctions(String formula)
		{
			for (Func f : ALL_FUNCS)
				formula = resolveFunc(f, formula);
			return formula;
		}
		
		static String resolveFunc(Func func, String formula)
		{
			String prefix = func.id + "(";
			String processing = formula;
			while(!processing.isEmpty())
			{
				if(!processing.startsWith(prefix))
				{
					processing = processing.substring(1);
					continue;
				}
				int depth = 1;
				String content = processing.substring(prefix.length());
				for (int i = 0; i < content.length(); i++)
				{
					if(content.charAt(i) == '(')
						depth++;
					else if(content.charAt(i) == ')')
					{
						if(--depth <= 0)
						{
							content = content.substring(0, i);
							break;
						}
					}
				}
				String[] args = content.split(",");
				if(args.length == 1 && args[0].isEmpty())
					args = new String[0];
				Float[] processedArgs = new Float[args.length];
				for (int i = 0; i < args.length; i++)
					processedArgs[i] = new Calculatable(args[i]).calculate(Map.of());
				formula = formula.replace(prefix + content + ")", String.valueOf(func.processor.apply(processedArgs)));
				processing = processing.substring(prefix.length() + content.length() + 1);
			}
			return formula;
		}
	}
}
