package absolutelyaya.goop.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Calculatable(String formula)
{
	public static final Codec<Calculatable> CODEC = Codec.either(Codec.stringResolver(Calculatable::formula, Calculatable::new),
					Codec.FLOAT.xmap(i -> new Calculatable(String.valueOf(i)), i -> i.calculate(Map.of()))).xmap(Either::unwrap, Either::left);
	
	public float calculate(Map<String, Float> vars)
	{
		String curFormula = formula;
		for (Map.Entry<String, Float> var : vars.entrySet())
			curFormula = curFormula.replace(var.getKey(), var.getValue().toString());
		final Pattern pattern = Pattern.compile("[A-Za-z]+");
		final Matcher matcher = pattern.matcher(curFormula);
		while(matcher.find())
			curFormula = curFormula.replace(matcher.group(), "0");
		return eval(curFormula);
	}
	
	float eval(String equation)
	{
		final Pattern bracketPattern = Pattern.compile("\\((.*?)\\)", Pattern.CASE_INSENSITIVE);
		final Matcher bracketMatcher = bracketPattern.matcher(equation);
		while(bracketMatcher.find())
		{
			String i = bracketMatcher.group();
			equation = equation.replace(i, String.valueOf(eval(i.replace("(", "").replace(")", ""))));
		}
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
			if(list.size() == 3 || list.size() == 4)
				return DataResult.success(new Color(list.get(0), list.get(1), list.get(2), list.size() == 4 ? list.get(3) : new Calculatable("1")));
			return DataResult.error(() -> "Input is not a List of 3 or 4 Elements.");
		}
		
		public int getColor(Map<String, Float> vars)
		{
			return new java.awt.Color(r.calculate(vars), g.calculate(vars), b.calculate(vars), a.calculate(vars)).getRGB();
		}
	}
}
